package tech.wenisch.discord.experiencebot.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionExpRepository extends JpaRepository<SessionExp, Long> {

    @Query("SELECT SUM(se.exp) FROM SessionExp se WHERE se.member = :member AND se.session.guild = :guild")
    Integer sumExpByMemberAndGuild(@Param("member") String member, @Param("guild") String guild);

    @Query(value = "SELECT session_exp.member FROM session_exp INNER JOIN sessions on session_exp.session = sessions.id WHERE sessions.guild = :guild GROUP BY session_exp.member ORDER BY SUM(session_exp.exp) DESC LIMIT 10", nativeQuery = true)
    List<String> topUsersNative(@Param("guild") String guild);
}
