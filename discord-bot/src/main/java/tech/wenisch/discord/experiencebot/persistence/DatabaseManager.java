package tech.wenisch.discord.experiencebot.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import tech.wenisch.discord.experiencebot.ExperienceManager;
import tech.wenisch.discord.experiencebot.SentryManager;

@Component
public class DatabaseManager {

	private JdbcTemplate jdbcTemplate;
	private static final DatabaseManager instance = new DatabaseManager();
	public static DatabaseManager getInstance(){
		return instance;
	}
	private DatabaseManager() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5432/experience");
		driverManagerDataSource.setUsername("user");
		driverManagerDataSource.setPassword("topsecret");
		jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
	}

	public void saveSession(String guildID, String userID, long startTime, long endTime) {

		Integer sessionID = storeSessionInfo(guildID, userID, String.valueOf(startTime), String.valueOf(endTime));
		storeSessionExp(ExperienceManager.generateEXPFromSession(startTime, endTime), sessionID, userID);

	}

	private Integer storeSessionExp(long generateEXPFromSession, Integer sessionID, String userID) {
		Integer primaryKey = null;
		String sql = "INSERT INTO " + "session_exp (session, member, exp) " + "VALUES(?, ?, ?)";
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(conn -> {

			// Pre-compiling SQL
			PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			// Set parameters
			preparedStatement.setInt(1, sessionID);
			preparedStatement.setString(2, userID);
			preparedStatement.setInt(3, Integer.valueOf(String.valueOf(generateEXPFromSession)));

			return preparedStatement;

		}, generatedKeyHolder);
		if (generatedKeyHolder.getKeys().size() > 1) {
			primaryKey = (int) generatedKeyHolder.getKeys().get("id");
		} else {
			primaryKey = generatedKeyHolder.getKey().intValue();
		}
		return primaryKey;
	}

	private Integer storeSessionInfo(String guildID, String userID, String startTime, String endTime) {
		Integer primaryKey = null;
		String sql = "INSERT INTO " + "sessions (guild, member, starttime, endtime) " + "VALUES(?, ?, ?, ?)";
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(conn -> {

			// Pre-compiling SQL
			PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			// Set parameters
			preparedStatement.setString(1, guildID);
			preparedStatement.setString(2, userID);
			preparedStatement.setString(3, startTime);
			preparedStatement.setString(4, endTime);

			return preparedStatement;

		}, generatedKeyHolder);
		if (generatedKeyHolder.getKeys().size() > 1) {
			primaryKey = (int) generatedKeyHolder.getKeys().get("id");
		} else {
			primaryKey = generatedKeyHolder.getKey().intValue();
		}

		// System.out.println("Saved session " + sessionID + " for " + userID + " on " +
		// guildID);

		return primaryKey;
	}

	public String getTotalExp(String userID, String guildID) {
		String sql = "SELECT sum(exp) as total_exp FROM session_exp  INNER JOIN sessions on session_exp.session = sessions.id  where session_exp.member ='"
				+ userID + "' and sessions.guild='" + guildID + "'";
		String totalEXP = "0";

		try {
			totalEXP = (String) jdbcTemplate.queryForObject(sql, String.class);
		} catch (Exception ex) {
			SentryManager.getInstance().handleError(ex);
		}
		return totalEXP;
	}

	public List<String> getTopFiveUsers() {
		List<String> userIDs = new ArrayList<String>();
		String sql = "SELECT member, sum(exp) as total_exp FROM session_exp GROUP BY member ORDER BY total_exp DESC LIMIT (5)";
		try {
			jdbcTemplate.query(sql, new RowCallbackHandler() {
				public void processRow(ResultSet resultSet) throws SQLException {
					while (resultSet.next()) {
						userIDs.add(resultSet.getString(1));
					}
				}
			});

		} catch (Exception ex) {
			SentryManager.getInstance().handleError(ex);
		}
		return userIDs;
	}

	public List<String> getRegulars(String guildID) {
		List<String> userIDs = new ArrayList<String>();

		String sql = "SELECT * FROM ( SELECT member, SUM (diff_minutes) as spendtime, count(*) as visits FROM ( SELECT  * ,extract(epoch FROM to_timestamp(CAST(endtime as bigint)/1000) - to_timestamp(CAST(starttime as bigint)/1000))/ 60 as diff_minutes FROM sessions WHERE  to_timestamp(CAST(endtime as bigint)/1000)  >  NOW() - INTERVAL '14 days' and guild='"
				+ guildID + "' ) as timedifftable GROUP BY member ) as resulttable WHERE spendtime > 60 AND visits > 3";
		try {
			jdbcTemplate.query(sql, new RowCallbackHandler() {
				public void processRow(ResultSet resultSet) throws SQLException {
					while (resultSet.next()) {
						userIDs.add(resultSet.getString("member"));
					}
				}
			});

		} catch (Exception ex) {
			SentryManager.getInstance().handleError(ex);
		}
		return userIDs;
	}
}
