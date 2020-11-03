package com.alexcorp.bloggers.statistics;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.service.UserService;
import com.alexcorp.bloggers.statistics.domains.OverviewSnapshot;
import com.alexcorp.bloggers.statistics.repositories.OverviewSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class ServerStatisticManager {

    public static final int GUESTS = 0;
    public static final int STUDENTS = 1;
    public static final int TEACHERS = 2;
    public static final int ADMINS = 3;

    private static final Logger log = LoggerFactory.getLogger(ServerStatisticManager.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private SystemUsageInfo systemUsageInfo;

    @Autowired
    private ApplicationContext appContext;

    private UserService userService;

    @Autowired
    private OverviewSnapshotRepository overviewSnapshotRepository;

    private Set<String>[] requestAddrs = new Set[]{new HashSet(), new HashSet(), new HashSet(), new HashSet()};

    @Scheduled(cron = "0 0/5 * * * ?")
    public void saveServerStateStatisticsSnapshot() {
        overviewSnapshotRepository.save(getSnapshot());

        unset();
    }

    public OverviewSnapshot getSnapshot(){
        return new OverviewSnapshot(new Date(),
                requestAddrs[ADMINS].size(),
                requestAddrs[TEACHERS].size(),
                requestAddrs[STUDENTS].size(),
                requestAddrs[GUESTS].size(),
                (float)systemUsageInfo.getProcessCPUUsage(),
                (float)systemUsageInfo.getTotalCPUusage(),
                (float)systemUsageInfo.getRAMusage());
    }

    public void addRequest(String requestAddr){
        if(userService == null) {
            try{
                userService = appContext.getBean(UserService.class);
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        User user = userService.loadUserFromSession();

        if(user == null){
            sortRequest(GUESTS, requestAddr);
            return;
        }

        if(user.getAuthorities().contains(User.Role.COMPANY_REPRES)
            || user.getAuthorities().contains(User.Role.ADMIN)) {
            sortRequest(ADMINS, requestAddr);
            return;
        }

        if(user.getAuthorities().contains(User.Role.Teacher)){
            sortRequest(TEACHERS, requestAddr);
            return;
        }

        if(user.getAuthorities().contains(User.Role.Student)){
            sortRequest(STUDENTS, requestAddr);
            return;
        }
    }

    private void sortRequest(int role, String addr) {
        requestAddrs[role].add(addr);

        for(int i = 0; i <= 3; i++) if(i != role) requestAddrs[i].remove(addr);
    }

    private void unset(){
        for(int i = 0; i < requestAddrs.length; i++){
            requestAddrs[i].clear();
        }
    }
}