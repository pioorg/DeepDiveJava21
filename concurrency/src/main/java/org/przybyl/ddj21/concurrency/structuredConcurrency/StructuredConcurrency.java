/*
 *  Copyright (C) 2023 Piotr Przybył
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.przybyl.ddj21.concurrency.structuredConcurrency;


import org.przybyl.ddj21.concurrency.virtThreads.VirtThreadsLimits;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredTaskScope;


public class StructuredConcurrency {

    static NameService nameService = new NameService();
    static ScoreService scoreService = new ScoreService();

    public static void main(String[] args) {
        var start = Instant.now();
        Map<String, Integer> result = Map.of();
        try {
//            result = sequentialScoreBoard();
//            result = futuredScoreBoard();
//            result = structuredScoreBoard();
            result = misusedScoreBoard();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // in production code most probably you don't want to ignore exceptions like this
            // here is demo only
        } finally {
            var took = Duration.between(start, Instant.now());
            System.out.printf("It took %s%n", took);
            System.out.println(result);
        }
    }

    static Map<String, Integer> sequentialScoreBoard() {
        var names = nameService.getNames();
        var scores = scoreService.getScores();
        return combine(names, scores);
    }

    static Map<String, Integer> futuredScoreBoard() throws ExecutionException, InterruptedException {
        try (var es = Executors.newVirtualThreadPerTaskExecutor()) {
            var namesFuture = es.submit(() -> nameService.getNames());
            var scoresFuture = es.submit(() -> scoreService.getScores());
            var names = namesFuture.get();
            var scores = scoresFuture.get();
            return combine(names, scores);
        }
    }

    static Map<String, Integer> structuredScoreBoard() throws ExecutionException, InterruptedException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var namesSubtask = scope.fork(() -> nameService.getNames());
            var scoresSubtask = scope.fork(() -> scoreService.getScores());
            scope.join();
            scope.throwIfFailed();
            var names = namesSubtask.get();
            var scores = scoresSubtask.get();
            return combine(names, scores);
        }
    }

    static Map<String, Integer> misusedScoreBoard() throws ExecutionException, InterruptedException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var namesSubtask = scope.fork(() -> nameService.getNames());
            var scoresSubtask = scope.fork(() -> scoreService.getScores());
//            new Thread(() -> scope.fork(() -> {
//                System.out.println("fork outside");
//                return null;
//            })).start();
            scope.join();
//            scope.throwIfFailed();
            var names = namesSubtask.get();
            var scores = scoresSubtask.get();
            return combine(names, scores);
        }
    }


    static Map<String, Integer> combine(List<UserName> names, List<UserScore> scores) {
        // this is extremely naive implementation, for demo only
        // never write production code like this, pretty please
        return Map.of(
            names.get(0).name(), scores.get(0).score(), //
            names.get(1).name(), scores.get(1).score(), //
            names.get(2).name(), scores.get(2).score()  //
        );
    }

    static void oops() {
        throw new RuntimeException("Oppsies ;-)");
    }
}


record UserName(int id, String name) {
}

record UserScore(int id, int score) {
}

class NameService {
    List<UserName> getNames() {
//        StructuredConcurrency.oops();
        VirtThreadsLimits.sneakySleep(1_000);
//        StructuredConcurrency.oops();
        return List.of(
            new UserName(1, "Joe"),
            new UserName(2, "Susan"),
            new UserName(2, "Krzysztof")
        );
    }


}

class ScoreService {
    List<UserScore> getScores() {
//        StructuredConcurrency.oops();
        VirtThreadsLimits.sneakySleep(1_000);
//        StructuredConcurrency.oops();
        return List.of(
            new UserScore(1, 100),
            new UserScore(2, 100),
            new UserScore(2, 300)
        );
    }
}