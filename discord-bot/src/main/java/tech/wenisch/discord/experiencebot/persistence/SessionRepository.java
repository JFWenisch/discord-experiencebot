package tech.wenisch.discord.experiencebot.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

	@Query(value = " SELECT se.member FROM session_exp se INNER JOIN sessions s ON se.session = s.id WHERE s.guild = :guild AND s.start_time >= NOW() - INTERVAL 14 DAY GROUP BY se.member HAVING COUNT(DISTINCT se.session) > 3 AND SUM(se.exp) > 3600 ", nativeQuery = true) 
	List<String> findRegulars(@Param("guild") String guild);
}
