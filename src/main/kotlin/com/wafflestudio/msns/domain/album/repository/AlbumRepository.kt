package com.wafflestudio.msns.domain.album.repository

import com.wafflestudio.msns.domain.album.model.Album
import org.springframework.data.jpa.repository.JpaRepository

interface AlbumRepository : JpaRepository<Album, Long?>
