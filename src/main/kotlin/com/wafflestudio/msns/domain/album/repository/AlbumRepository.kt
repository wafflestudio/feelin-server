package com.wafflestudio.msns.domain.album.repository

import com.wafflestudio.msns.domain.album.model.Album
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumRepository : JpaRepository<Album, UUID?>
