package com.example;

import com.example.dao.EntityDao;
import com.example.entity.PlayerEntity;
import com.example.models.Player;
import com.example.models.Symbol;

import java.util.Random;
import java.util.Scanner;


public class GameEngine {
    private Player human;
    private Player ai;
    private EntityDao<PlayerEntity> genericUserDao;
    private Scanner scanner;

    public GameEngine(Player human, Player ai, EntityDao<PlayerEntity> genericUserDao) {
        this.human = human;
        this.ai = ai;
        this.genericUserDao = genericUserDao;
        this.scanner = new Scanner(System.in);
    }

    public void startNewGame() {
        System.out.println("Player enter your name:");
        human.setNickname(scanner.nextLine());
        System.out.println("Hello " + human.getNickname());
        ai.setNickname("AI");

        PlayerEntity humanEntity = new PlayerEntity(human.getNickname(), human.getScore());
        PlayerEntity aiEntity = new PlayerEntity(ai.getNickname(), ai.getScore());
        savePlayersEntitiesToDB(humanEntity, aiEntity);
        getEntitiesIDandSaveToPlayers(humanEntity, aiEntity);

        match(humanEntity, aiEntity);
    }

    public void loadPreviousMatch() {
        PlayerEntity humanEntity = genericUserDao.getById(human.getId());
        PlayerEntity aiEntity = genericUserDao.getById(ai.getId());

        setupPlayersFromDB(human, ai, humanEntity, aiEntity);
        int currentRound = countRoundNumber(human, ai) + 1;

        match(humanEntity, aiEntity);
    }

    private void match(PlayerEntity humanEntity, PlayerEntity aiEntity) {
        while (!hasWonn(human, ai)) {
            System.out.println("\n\n-----NEXT ROUND----- ");
            printCurrentScore(human, ai);
            humanMove();
            aiMove();
            increaseWinnersScore(human.getSymbol(), ai.getSymbol());
            System.out.println();
            updatePlayersDB(humanEntity, aiEntity);
            System.out.println("Need a break? y/n");
            String decision = scanner.nextLine();
            if (decision.equals("y") || decision.equals("Y")) {
                System.out.println("Ending current match");
                System.out.println("Players id: " + human.getId() + " & AI id: " + ai.getId());
                break;
            }
        }
        if (hasWonn(human, ai)){
            printWinner();
        }
    }

    private boolean hasWonn(Player human, Player ai) {
        return human.getScore() >= 3 || ai.getScore() >= 3;
    }

    private void printCurrentScore(Player human, Player ai) {
        System.out.println(human.getNickname() + " " + human.getScore() + " vs AI " + ai.getScore());
    }

    private void setupPlayersFromDB(Player human, Player ai, PlayerEntity humanEntity, PlayerEntity aiEntity) {
        human.setNickname(humanEntity.getNickname());
        human.setScore(humanEntity.getScore());
        ai.setNickname(aiEntity.getNickname());
        ai.setScore(aiEntity.getScore());
    }

    private void getEntitiesIDandSaveToPlayers(PlayerEntity humanEntity, PlayerEntity aiEntity) {
        human.setId(humanEntity.getId());
        ai.setId(aiEntity.getId());
    }

    private void savePlayersEntitiesToDB(PlayerEntity humanEntity, PlayerEntity aiEntity) {
        genericUserDao.save(humanEntity);
        genericUserDao.save(aiEntity);
    }

    private void updatePlayersDB(PlayerEntity humanEntity, PlayerEntity aiEntity) {
        humanEntity.setScore(human.getScore());
        genericUserDao.update(humanEntity);
        aiEntity.setScore(ai.getScore());
        genericUserDao.update(aiEntity);
    }

    private void printWinner() {
        if (human.getScore() == ai.getScore()) {
            System.out.println("Its a tie!");
        } else {
            System.out.println(human.getScore() > ai.getScore() ? "You wonn!" : "Ai wonn!");
        }
    }

    private void increaseWinnersScore(Enum<Symbol> humanSymbol, Enum<Symbol> aiSymbol) {
        if (humanSymbol.equals(aiSymbol)) {
            human.setScore(human.getScore() + 1);
            ai.setScore(ai.getScore() + 1);
        } else if (humanSymbol.equals(Symbol.ROCK) && (aiSymbol.equals(Symbol.SCISSORS))) {
            human.setScore(human.getScore() + 1);
        } else if (humanSymbol.equals(Symbol.SCISSORS) && (aiSymbol.equals(Symbol.PAPER))) {
            human.setScore(human.getScore() + 1);
        } else if (humanSymbol.equals(Symbol.PAPER) && (aiSymbol.equals(Symbol.ROCK))) {
            human.setScore(human.getScore() + 1);
        } else {
            ai.setScore(ai.getScore() + 1);
        }
    }

    private int countRoundNumber(Player human, Player ai) {
        int score = 0;
        if (human.getScore() == ai.getScore()) {
            score = human.getScore();
        } else if (human.getScore() > ai.getScore()) {
            score = human.getScore() + (human.getScore() - ai.getScore());
        } else {
            score = ai.getScore() + (ai.getScore() - human.getScore());
        }
        return score;
    }

    private void aiMove() {
        Random random = new Random();
        int symbol = random.nextInt(3) + 1;
        ai.setSymbol(symbol);
        System.out.println(ai.getNickname() + " picked " + ai.getSymbol());
    }

    private void humanMove() {
        System.out.println("Pick a number [1-3]:");
        System.out.println("1.ROCK, 2.PAPER, 3.SCISSORS");
        Scanner scanner = new Scanner(System.in);
        int symbol = scanner.nextInt();
        human.setSymbol(symbol);
    }
}
