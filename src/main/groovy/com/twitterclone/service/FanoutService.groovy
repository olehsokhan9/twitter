package com.twitterclone.service

import com.twitterclone.domain.wall.Wall
import com.twitterclone.domain.tasks.PostCreatedFanoutTask
import com.twitterclone.repository.PostCreatedFanoutTaskRepository
import com.twitterclone.repository.PostRepository
import com.twitterclone.repository.WallRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.Duration
import java.util.stream.Stream

import static com.twitterclone.domain.tasks.PostCreatedFanoutTaskState.CREATED
import static com.twitterclone.domain.tasks.PostCreatedFanoutTaskState.IN_PROGRESS
import static com.twitterclone.domain.wall.WallCompositeKeyProvider.compositeId
import static java.time.Instant.now

@Service
class FanoutService {

    private static final Logger LOG = LoggerFactory.getLogger(FanoutService.class);

    private static final String LOCK_KEY = "process-subscription-tasks-lock-key"
    private static final Duration LOCK_KEY_EXPIRE_TIME = Duration.ofSeconds(30)
    private static final int TASKS_FETCH_SIZE = 50
    private static final Duration TASK_MAX_PROCESSING_TIME = Duration.ofMinutes(2) // TODO: should be dynamic based on number of followers

    private final FollowersService followersService
    private final LockService lockService
    private final PostCreatedFanoutTaskRepository postCreatedFanoutTaskRepository
    private final WallRepository wallRepository
    private final PostRepository postRepository

    FanoutService(FollowersService followersService,
                  LockService lockService,
                  PostCreatedFanoutTaskRepository postCreatedFanoutTaskRepository,
                  WallRepository wallRepository,
                  PostRepository postRepository) {
        this.followersService = followersService
        this.lockService = lockService
        this.postCreatedFanoutTaskRepository = postCreatedFanoutTaskRepository
        this.wallRepository = wallRepository
        this.postRepository = postRepository
    }

    @Scheduled(fixedDelay = 1_000)
    void process() {
        if (!lockService.acquireLock(LOCK_KEY, LOCK_KEY_EXPIRE_TIME.toMillis())) {
            return
        }

        // todo: for posts with huge number of followers another strategy can be applied, fan-out read
        List<PostCreatedFanoutTask> tasksToProcess = null
        try {
            final def pageRequest = PageRequest.of(0, TASKS_FETCH_SIZE)
            final def created = postCreatedFanoutTaskRepository
                    .findByStateAndProcessAtLessThan(CREATED, Date.from(now()), pageRequest)
            // todo: combine into a single query, or split into separate jobs
            final def inProgressExpired = postCreatedFanoutTaskRepository
                    .findByStateAndProcessAtLessThan(IN_PROGRESS, Date.from(now()), pageRequest)

            tasksToProcess = Stream.concat(created.stream(), inProgressExpired.stream())
                    .map(it -> it.inProgress(TASK_MAX_PROCESSING_TIME))
                    .toList()

            postCreatedFanoutTaskRepository.saveAll(tasksToProcess)
        } finally {
            lockService.releaseLock(LOCK_KEY)
        }

        tasksToProcess.forEach(task -> {
            try {
                processForFollowee(task.postId(), task.userId())
                postCreatedFanoutTaskRepository.deleteById(task.postId())
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex)
            }
        })
    }

    private void processForFollowee(UUID postId, UUID followeeId) {
        // TODO: introduce pagination, split this into batches and assign to workers, parallelization on each node could be applied
        final def followers = followersService.getFollowerIds(followeeId)

        final def post = postRepository.findById(postId)
        if (post.isEmpty()) {
            LOG.info("post %s no longer exists, skipping".formatted(postId))
            return
        }

        final def postCreatedDate = post.orElseThrow().createdDate()
        followers.forEach(followerId -> {
            final def compositeId = compositeId(followerId, postId)
            final def wall = new Wall(compositeId, followerId, postId, followeeId, postCreatedDate)
            wallRepository.save(wall)
        })
    }
}
