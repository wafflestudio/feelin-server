package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.Follow
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<Follow, Long?> {

}
