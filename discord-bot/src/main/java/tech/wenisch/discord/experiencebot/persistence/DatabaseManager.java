package tech.wenisch.discord.experiencebot.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import tech.wenisch.discord.experiencebot.OnlineListener;

public class DatabaseManager {

	private static Connection connection =null;
	public DatabaseManager()
	{
		getConnection();
	}
	public  Connection getConnection() {
		if (connection== null) 
		{
			String url = System.getenv("DATABASE_URL");
			String user = System.getenv("DATABASE_USER");
			String password = System.getenv("DATABASE_PASSWORD");

			try {
				connection = DriverManager.getConnection(url, user, password);
				System.out.println("Successfully connected to database "+url);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return connection;
	}

	public  void saveSession(String guildID, String userID, long startTime, long endTime) {

		Integer sessionID= storeSessionInfo(guildID,userID,String.valueOf(startTime),String.valueOf(endTime));
		storeSessionExp(OnlineListener.generateEXPFromSession(startTime, endTime),sessionID, userID);

	}
	private void storeSessionExp(long generateEXPFromSession, Integer sessionID, String userID) {
		String sql = "INSERT INTO "
				+ "session_exp (session, member, exp) "
				+ "VALUES(?, ?, ?)";


		try (PreparedStatement statement = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS)) 
		{

			statement.setInt(1,sessionID);
			statement.setString(2, userID);
			statement.setInt(3, Integer.valueOf(String.valueOf(generateEXPFromSession)));
			statement.executeUpdate();



			System.out.println("Saved  "+generateEXPFromSession+" EXP for "+userID+" based on session "+ sessionID);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}


	}
	private Integer storeSessionInfo(String guildID, String userID, String startTime, String endTime) 
	{
		String sql = "INSERT INTO "
				+ "sessions (guild, member, starttime, endtime) "
				+ "VALUES(?, ?, ?, ?)";

		Integer sessionID = null;
		try (PreparedStatement statement = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS)) 
		{

			statement.setString(1, guildID);
			statement.setString(2, userID);
			statement.setString(3, startTime);
			statement.setString(4, endTime);
			int numberOfInsertedRows = statement.executeUpdate();


			// Retrieve the auto-generated id
			if (numberOfInsertedRows > 0) 
			{
				try (ResultSet resultSet = statement.getGeneratedKeys())
				{
					if (resultSet.next()) 
					{
						sessionID = resultSet.getInt(1);
					}
				}
			}
			System.out.println("Saved session "+sessionID+" for "+userID+" on "+ guildID);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return sessionID;
	}
	public String getTotalExp(String userID, String guildID)
	{
		String sql = "SELECT sum(exp) as total_exp FROM session_exp  INNER JOIN sessions on session_exp.session = sessions.id  where session_exp.member ='"
				+ userID
				+ "' and sessions.guild='"
				+ guildID
				+ "'";

		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			if (resultSet.next()) {
				return resultSet.getString(1);

			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return "0";
	}
	public List<String> getTopFiveUsers()
	{
		List<String> userIDs = new ArrayList<String>();
		String sql = "SELECT member, sum(exp) as total_exp FROM session_exp GROUP BY member ORDER BY total_exp DESC LIMIT (5)";

		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				userIDs.add(resultSet.getString(1));

			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return userIDs;
	}
	public List<String> getRegulars(String guildID)
	{
		List<String> userIDs = new ArrayList<String>();
	
		String sql = "SELECT * FROM ( SELECT member, SUM (diff_minutes) as spendtime, count(*) as visits FROM ( SELECT  * ,extract(epoch FROM to_timestamp(CAST(endtime as bigint)/1000) - to_timestamp(CAST(starttime as bigint)/1000))/ 60 as diff_minutes FROM sessions WHERE  to_timestamp(CAST(endtime as bigint)/1000)  >  NOW() - INTERVAL '14 days' and guild='"+guildID+"' ) as timedifftable GROUP BY member ) as resulttable WHERE spendtime > 60 AND visits > 3";

		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				userIDs.add(resultSet.getString("member"));
				System.out.println("Aggregated for member "+resultSet.getString("member")+"  "+resultSet.getString("visits")+" visits and " +resultSet.getString("spendtime")+" spend minutes in total");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return userIDs;
	}
}
