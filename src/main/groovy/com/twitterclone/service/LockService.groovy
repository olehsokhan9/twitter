package com.twitterclone.service

import com.mongodb.DuplicateKeyException
import com.twitterclone.domain.Lock
import com.twitterclone.repository.LockRepository
import org.springframework.stereotype.Service

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState
import static java.lang.System.currentTimeMillis

@Service
class LockService {

    private final LockRepository lockRepository

    LockService(LockRepository lockRepository) {
        this.lockRepository = lockRepository
    }

    boolean acquireLock(String key, long ttlMillis) {
        checkNotNull(key)
        checkState(ttlMillis > 0)

        final def expireAt = new Date(currentTimeMillis() + ttlMillis)
        try {
            lockRepository.insert(new Lock(key, expireAt))
            return true
        } catch (DuplicateKeyException ignore) {
            // Lock already exists
            return false
        }
    }

    void releaseLock(String key) {
        lockRepository.deleteById(key)
    }
}
