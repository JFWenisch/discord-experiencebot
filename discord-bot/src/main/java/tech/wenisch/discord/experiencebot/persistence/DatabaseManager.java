package tech.wenisch.discord.experiencebot.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import tech.wenisch.discord.experiencebot.OnlineListener;
import tech.wenisch.discord.experiencebot.TimeUtils;

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
	public String getTotalExp(String userID)
	{
		String sql = "SELECT sum(exp) as total_exp FROM session_exp where member ='"
				+ userID
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
}
