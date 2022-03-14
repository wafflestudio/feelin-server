package com.wafflestudio.msns.domain.artist.repository

import com.wafflestudio.msns.domain.artist.model.Artist
import org.springframework.data.jpa.repository.JpaRepository

interface ArtistRepository : JpaRepository<Artist, Long?>
