package com.wafflestudio.msns.domain.artist.repository

import com.wafflestudio.msns.domain.artist.model.Artist
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArtistRepository : JpaRepository<Artist, UUID?>
