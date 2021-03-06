package com.example;

import com.example.dao.EntityDao;
import com.example.dao.PlayerDao;
import com.example.entity.PlayerEntity;
import com.example.models.Player;

import java.util.Scanner;

import static com.example.Menu.startMenu;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HibernateFactory hibernateFactory = new HibernateFactory();
        EntityDao<PlayerEntity> genericUserDao = new PlayerDao(hibernateFactory, PlayerEntity.class);

        boolean exitProgram = false;
        while (!exitProgram){
            int getMenuOption = startMenu();
            switch (getMenuOption){
                case 1:
                    boolean matchOver = false;
                    while(!matchOver){
                        GameEngine gameEngine = new GameEngine(
                                new Player(true, 0),
                                new Player(false, 0),
                                genericUserDao);
                        gameEngine.startNewGame();
                        System.out.println("Start new match? (y/n)");
                        String decision = scanner.nextLine();
                        if (decision.equals("n") || decision.equals("N")){
                            matchOver = true;
                        }
                    }
                    break;
                case 2:
                    System.out.println("Enter id of the player you would like to resume: ");
                    int resumeID = scanner.nextInt();
                    GameEngine gameEngine = new GameEngine(
                            new Player(resumeID),
                            new Player(resumeID+1),
                            genericUserDao);
                    gameEngine.loadPreviousMatch();
                    break;
                case 3:
                    exitProgram = true;
                    break;
            }
        }

    }
}
