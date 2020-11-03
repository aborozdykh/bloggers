package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.dto.Card;
import com.alexcorp.bloggers.dto.CommandStatus;
import com.alexcorp.bloggers.dto.DoughnutCard;
import com.alexcorp.bloggers.dto.ValueCard;
import com.alexcorp.bloggers.statistics.CommandExecutor;
import com.alexcorp.bloggers.statistics.ServerStatisticManager;
import com.alexcorp.bloggers.statistics.domains.OverviewSnapshot;
import com.alexcorp.bloggers.statistics.repositories.OverviewSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alexcorp.bloggers.dto.DoughnutCard.DEFAULT;
import static com.alexcorp.bloggers.dto.DoughnutCard.PER_CENT_TYPE;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminPanelController {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("DD MM YYYY HH:mm");
    private final static SimpleDateFormat dateFormat2 = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");

    private final static long MILiSEC_IN_HOUR = 60 * 60 * 1000;
    private final static long SECONDS_IN_DAY = 24 * 60 * 60 * 1000;
    private final static long SECONDS_IN_WEEK = 7 * SECONDS_IN_DAY;

    private final static int TODAY = 0;
    private final static int YESTERDAY = 1;
    private final static int LAST_WEEK = 7;
    private final static int LAST_4_WEEK = 28;

    private final static int UPDATE_RESOURCES = 14;
    private final static int RESTART = 156;
    private final static int BUILD = 127;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private ServerStatisticManager statisticManager;

    @Autowired
    private OverviewSnapshotRepository overviewSnapshotRepository;

    @GetMapping("/adminpanel/**")
    String adminpanel(HttpSession session, Model model){
        model.addAttribute("isDevMode", profile.equals("dev"));
        model.addAttribute("nickname", session.getAttribute("nickname"));
        model.addAttribute("role", session.getAttribute("role"));
        return "adminpanel";
    }

    @GetMapping("/rest/overview")
    public @ResponseBody Object overview(){
        OverviewSnapshot snapshot = statisticManager.getSnapshot();

        Map<String, Map<String, ? extends Card>> json = new HashMap<>();
        Map<String, ValueCard> valueCards = new HashMap<>();
        Map<String, DoughnutCard> doughnutCards = new HashMap<>();

        doughnutCards.put("1", new DoughnutCard<Integer>("Online", 0, DEFAULT, new HashMap<String, Integer>(){
                                                                            {put("adminsOnline", snapshot.getAdminsOnline());}
                                                                            {put("teachersOnline", snapshot.getTeachersOnline());}
                                                                            {put("studentsOnline", snapshot.getStudentsOnline());}
                                                                            {put("guestsOnline", snapshot.getGuestsOnline());}
                                                                        }));

        doughnutCards.put("2", new DoughnutCard<Float>("CPU Load", snapshot.getTotalCpuUsage(), PER_CENT_TYPE, new HashMap<String, Float>(){
            {put("systemCpuUsage", snapshot.getTotalCpuUsage() - snapshot.getProcessCpuUsage());}
            {put("processCpuUsage", snapshot.getProcessCpuUsage());}
            {put("free", 100 - snapshot.getTotalCpuUsage());}
        }));

        doughnutCards.put("3", new DoughnutCard<Float>("RAM Load", snapshot.getRamUsage(), PER_CENT_TYPE, new HashMap<String, Float>(){
            {put("systemRamUsage", snapshot.getRamUsage());}
            {put("free", 100 - snapshot.getRamUsage());}
        }));

        json.put("value-cards", valueCards);
        json.put("doughnut-cards", doughnutCards);

        return json;
    }

    @GetMapping("/rest/snapshot")
    public @ResponseBody Object snapshot(@RequestParam int range){ //range = 0 || 1 || 7 || 28
        long timeNow = System.currentTimeMillis() + 3 * MILiSEC_IN_HOUR;
        Date rangeDateStart;
        Date rangeDateEnd;
        List<OverviewSnapshot> snapshots = null;

        switch(range){
            case TODAY: {
                rangeDateStart = new Date(timeNow - timeNow % SECONDS_IN_DAY - 3 * MILiSEC_IN_HOUR);
                rangeDateEnd = new Date(rangeDateStart.getTime() + SECONDS_IN_DAY );

                snapshots = overviewSnapshotRepository
                        .findAllByDateGreaterThanEqualAndDateLessThanOrderByDateAsc(rangeDateStart, rangeDateEnd);
                break;
            }
            case YESTERDAY: {
                rangeDateStart = new Date(timeNow - timeNow % SECONDS_IN_DAY - 1 * SECONDS_IN_DAY - 3 * MILiSEC_IN_HOUR);
                rangeDateEnd = new Date(rangeDateStart.getTime() + SECONDS_IN_DAY);
                snapshots = overviewSnapshotRepository
                        .findAllByDateGreaterThanEqualAndDateLessThanOrderByDateAsc(rangeDateStart, rangeDateEnd);
                break;
            }
            case LAST_WEEK: {
                rangeDateStart = new Date(timeNow - timeNow % SECONDS_IN_DAY - 7 * SECONDS_IN_DAY - 3 * MILiSEC_IN_HOUR);
                rangeDateEnd = new Date(rangeDateStart.getTime() + 7 * SECONDS_IN_DAY);
                snapshots = overviewSnapshotRepository
                        .findAllByDateGreaterThanEqualAndDateLessThanOrderByDateAsc(rangeDateStart, rangeDateEnd);
                break;
            }
            case LAST_4_WEEK: {
                rangeDateStart = new Date(timeNow - timeNow % SECONDS_IN_DAY - 28 * SECONDS_IN_DAY - 3 * MILiSEC_IN_HOUR);
                rangeDateEnd = new Date(rangeDateStart.getTime() + 28 * SECONDS_IN_DAY);
                snapshots = overviewSnapshotRepository
                        .findAllByDateGreaterThanEqualAndDateLessThanOrderByDateAsc(rangeDateStart, rangeDateEnd);
                break;
            }
            default:{
                System.out.println("Wrong time range!");
            }
        }


        Map<Integer, OverviewSnapshot> json = new HashMap<>();

        int count = 0;
        long lastDate = 0;

        if(snapshots != null){
            for(OverviewSnapshot snapshot : snapshots){
                if(snapshot.getDate().getTime() != lastDate){
                    json.put(count, snapshot);
                    lastDate = snapshot.getDate().getTime();
                    count++;
                }
            }
        }
        return json;
    }

    private String lastCommand;
    private String lastCommandOutput;
    private boolean lastCommandDone;

    @GetMapping("/rest/commands")
    public @ResponseBody String commands(@RequestParam int command){
        StringBuilder output = new StringBuilder();
        lastCommandDone = false;
        lastCommandOutput = "";

        switch(command){
            case UPDATE_RESOURCES: {
                lastCommand = "Updating Resources";
                System.out.println("\n[" + dateFormat2.format(new Date()) + "] - " + lastCommand + ":" + output.toString());
                CommandExecutor.runCommand(CommandExecutor.UPDATE_RESOURCES, output);
                System.out.println("[" + dateFormat2.format(new Date()) + "] - " + lastCommand + " - Done\n");
                lastCommandDone = true;
                lastCommandOutput = output.toString();
                break;
            }

            case BUILD: {
                lastCommand = "Rebuild";
                System.out.println("\n[" + dateFormat2.format(new Date()) + "] - " + lastCommand + ":" + output.toString());
                CommandExecutor.runCommand(CommandExecutor.REBUILD, output);
                System.out.println("[" + dateFormat2.format(new Date()) + "] - " + lastCommand + ": - Done\n");
                lastCommandDone = true;
                lastCommandOutput = output.toString();
                break;
            }

            case RESTART: {
                lastCommand = "Restart";
                System.out.println("\n[" + dateFormat2.format(new Date()) + "] - " + lastCommand + ":" + output.toString());
                CommandExecutor.runCommand(CommandExecutor.RESTART, output);
                lastCommandDone = true;
                lastCommandOutput = output.toString();
                break;
            }

        }

        return "{}";
    }

    @GetMapping("/rest/commands/status")
    public @ResponseBody CommandStatus status(){
        return new CommandStatus(lastCommand, lastCommandDone, lastCommandOutput);
    }
}