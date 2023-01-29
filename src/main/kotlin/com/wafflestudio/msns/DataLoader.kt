package com.wafflestudio.msns

import com.wafflestudio.msns.domain.album.model.Album
import com.wafflestudio.msns.domain.album.repository.AlbumRepository
import com.wafflestudio.msns.domain.artist.model.Artist
import com.wafflestudio.msns.domain.artist.repository.ArtistRepository
import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.playlist.repository.PlaylistRepository
import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.model.Follow
import com.wafflestudio.msns.domain.user.model.Like
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.FollowRepository
import com.wafflestudio.msns.domain.user.repository.LikeRepository
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.model.VerificationToken
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import com.wafflestudio.msns.global.enum.JWT
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.UUID

@Component
@Profile("none")
class DataLoader(
    private val userRepository: UserRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val playlistRepository: PlaylistRepository,
    private val postRepository: PostRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val followRepository: FollowRepository,
    private val likeRepository: LikeRepository
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

        val admin = User(
            email = "admin@feelin.com",
            countryCode = "82",
            phoneNumber = "010-1234-5678",
            password = passwordEncoder.encode("feelin-admin"),
            username = "admin",
            name = "John Doe",
            birthDate = LocalDate.of(2000, 1, 1),
        )

        val userA = User(
            email = "userA@feelin.com",
            countryCode = "82",
            phoneNumber = "010-0000-0001",
            password = passwordEncoder.encode("feelin-user"),
            username = "userA",
            name = "John Doe",
            birthDate = LocalDate.of(2000, 1, 1),
        )

        val userB = User(
            email = "userB@feelin.com",
            countryCode = "82",
            phoneNumber = "010-0000-0010",
            password = passwordEncoder.encode("feelin-user"),
            username = "userB",
            name = "John Doe",
            birthDate = LocalDate.of(2000, 1, 1),
        )

        userRepository.save(admin)
        userRepository.save(userA)
        userRepository.save(userB)

        val jwtA = jwtTokenProvider.generateToken(admin.id, JWT.ACCESS)
        val jwtB = jwtTokenProvider.generateToken(admin.id, JWT.REFRESH)
        val jwtC = jwtTokenProvider.generateToken(userA.id, JWT.ACCESS)
        val jwtD = jwtTokenProvider.generateToken(userA.id, JWT.REFRESH)
        val jwtE = jwtTokenProvider.generateToken(userB.id, JWT.ACCESS)
        val jwtF = jwtTokenProvider.generateToken(userB.id, JWT.REFRESH)

        val verificationTokenA = VerificationToken(
            email = admin.email,
            accessToken = jwtA,
            refreshToken = jwtB,
            authenticationCode = createRandomCode(),
            verified = true
        )

        val verificationTokenB = VerificationToken(
            email = userA.email,
            accessToken = jwtC,
            refreshToken = jwtD,
            authenticationCode = createRandomCode(),
            verified = true
        )

        val verificationTokenC = VerificationToken(
            email = userB.email,
            accessToken = jwtE,
            refreshToken = jwtF,
            authenticationCode = createRandomCode(),
            verified = true
        )

        verificationTokenRepository.save(verificationTokenA)
        verificationTokenRepository.save(verificationTokenB)
        verificationTokenRepository.save(verificationTokenC)

        val playlistA = Playlist(
            user = admin,
            playlistId = UUID.randomUUID(),
            playlistOrder = "1 2 3",
            thumbnail = "https://ibb.co/7R7kcgd"
        )

        val playlistB = Playlist(
            user = admin,
            playlistId = UUID.randomUUID(),
            playlistOrder = "6 5 3 2 4 1",
            thumbnail = "https://ibb.co/7R7kcgd"
        )

        playlistRepository.save(playlistA)
        playlistRepository.save(playlistB)

        val postA = Post(
            user = admin,
            title = "Do you love Jazz Piano?",
            content = "...",
            playlist = playlistA
        )

        postRepository.save(postA)

        val followA = Follow(
            fromUser = userA,
            toUser = admin
        )
        val followB = Follow(
            fromUser = userB,
            toUser = admin
        )

        followRepository.save(followA)
        followRepository.save(followB)

        val likeA = Like(
            user = userA,
            post = postA
        )
        val likeB = Like(
            user = userB,
            post = postA
        )

        likeRepository.save(likeA)
        likeRepository.save(likeB)
    }

    private fun createRandomCode(): String {
        return (100000..999999).random().toString()
    }
}
