package tech.wenisch.discord.experiencebot.persistence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tech.wenisch.discord.experiencebot.ExperienceManager;
import tech.wenisch.discord.experiencebot.SentryManager;

@Component
public class DatabaseManager {

    private final SessionRepository sessionRepository;
    private final SessionExpRepository sessionExpRepository;

    @Autowired
    public DatabaseManager(SessionRepository sessionRepository, SessionExpRepository sessionExpRepository) {
        this.sessionRepository = sessionRepository;
        this.sessionExpRepository = sessionExpRepository;
    }

    @Transactional
    public void saveSession(String guildID, String userID, long startTime, long endTime) {

        try {
            Session s = new Session();
            s.setGuild(guildID);
            s.setMember(userID);
            s.setStarttime(String.valueOf(startTime));
            s.setEndtime(String.valueOf(endTime));
            s = sessionRepository.save(s);

            SessionExp se = new SessionExp();
            se.setSession(s);
            se.setMember(userID);
            se.setExp(Integer.valueOf(String.valueOf(ExperienceManager.generateEXPFromSession(startTime, endTime))));
            sessionExpRepository.save(se);

        } catch (Exception ex) {
            SentryManager.getInstance().handleError(ex);
        }

    }

    public String getTotalExp(String userID, String guildID) {
        String totalEXP = "0";
        try {
            Integer total = sessionExpRepository.sumExpByMemberAndGuild(userID, guildID);
            if (total != null) {
                totalEXP = String.valueOf(total);
            }
        } catch (Exception ex) {
            SentryManager.getInstance().handleError(ex);
        }
        return totalEXP;
    }

    public List<String> getTopUsers(String guildID) {
        List<String> userIDs = new ArrayList<String>();
        try {
            userIDs = sessionExpRepository.topUsersNative(guildID);
        } catch (Exception ex) {
            SentryManager.getInstance().handleError(ex);
        }
        return userIDs;
    }

    public List<String> getRegulars(String guildID) {
        List<String> userIDs = new ArrayList<String>();
        try {
            userIDs = sessionRepository.findRegulars(guildID);
        } catch (Exception ex) {
            SentryManager.getInstance().handleError(ex);
        }
        return userIDs;
    }
}