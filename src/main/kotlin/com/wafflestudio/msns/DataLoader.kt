package com.wafflestudio.msns

import com.wafflestudio.msns.domain.album.model.Album
import com.wafflestudio.msns.domain.album.repository.AlbumRepository
import com.wafflestudio.msns.domain.artist.model.Artist
import com.wafflestudio.msns.domain.artist.repository.ArtistRepository
import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.playlist.repository.PlaylistRepository
import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.model.VerificationToken
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.UUID

@Component
@Profile("local", "dev")
class DataLoader(
    private val userRepository: UserRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val playlistRepository: PlaylistRepository,
    private val postRepository: PostRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val verificationTokenRepository: VerificationTokenRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {

        val artistA = Artist(
            artistName = "Jimmy Smith",
        )

        artistRepository.save(artistA)

        val albumA = Album(
            title = "THE SERMON!",
            description = "Blue Note 4011",
            releaseDate = LocalDate.ofYearDay(1959, 320),
            artist = artistA,
        )

        albumRepository.save(albumA)

        val userA = User(
            email = "admin@feelin.com",
            password = passwordEncoder.encode("feelin-admin"),
            username = "admin",
            firstName = "Doe",
            lastName = "John",
            phoneNumber = "010-1234-5678",
        )

        userRepository.save(userA)

        val jwtA = jwtTokenProvider.generateToken(userA.email, join = false)
        val verificationTokenA = VerificationToken(
            email = userA.email,
            token = passwordEncoder.encode(jwtA),
            authenticationCode = createRandomCode(),
            password = passwordEncoder.encode("feelin-admin"),
            verification = true
        )

        verificationTokenRepository.save(verificationTokenA)

        val playlistA = Playlist(
            user = userA,
            streamId = UUID.randomUUID(),
            thumbnail = "https://ibb.co/7R7kcgd"
        )

        val playlistB = Playlist(
            user = userA,
            streamId = UUID.randomUUID(),
            thumbnail = "https://ibb.co/7R7kcgd"
        )

        playlistRepository.save(playlistA)
        playlistRepository.save(playlistB)

        val postA = Post(
            user = userA,
            title = "Do you love Jazz Piano?",
            content = "...",
            playlist = playlistA
        )

        postRepository.save(postA)
    }

    private fun createRandomCode(): String {
        return (100000..999999).random().toString()
    }
}
