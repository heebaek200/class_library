package com.codingtest;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            boolean continueWhile = true;
            while (continueWhile) {
                System.out.println("================================");
                System.out.println("다음 중 입력하세요: bfs / dfs / exit");
                String command = scanner.nextLine();

                continueWhile = switch (command.trim()) {
                    case "bfs" -> {
                        new Main().bfs();
                        yield true;
                    }
                    case "dfs" -> {
                        new Main().dfs();
                        yield true;
                    }
                    case "exit" -> false;
                    default -> true;
                };
            }
        }

        System.out.println("프로그램 종료");
    }

    void dfs () {
        // 문제 예시: [0, 0] 에서 출발하여 이동할 수 있는 최대 칸 수를 출력하시오. 단, 이미 이동한 알파벳으로 이동할 수 없음.
        char[][] map = {
                {'A', 'B', 'C', 'D', 'E', 'F'},
                {'G', 'H', 'I', 'J', 'K', 'L'},
                {'M', 'N', 'O', 'P', 'Q', 'A'}
        };
        System.out.println("============= DFS과 백트랙킹 ==============");
        System.out.println("입력 :");
        for (char[] row : map) {
            System.out.println(Arrays.toString(row));
        }

        // 사전 자료 구조
        int[][] directions = new int[][] { {0, -1}, {0, 1}, {-1, 0}, {1, 0} };

        // 1. 필요한 자료 구조 정리
        HashSet<Character> visitedAlphabet = new HashSet<>();   // 방문한 알파벳 저장
        int[] maxDistance = {0};                                    // 최대 이동 거리 갱신

        // 2. 시작 지점 처리
        char currentChar = map[0][0];
        visitedAlphabet.add(currentChar);

        // 3. 백트랙킹 시작
        dfsFunc(0, 0, 1, map, directions, visitedAlphabet, maxDistance);

        // 출력
        System.out.println("출력 :");
        System.out.println(" - 최대거리");
        System.out.println(maxDistance[0]);
    }

    void dfsFunc (int x, int y, int count, char[][] map, int[][] directions, HashSet<Character> visitedAlphabet, int[] maxDistance) {
        // 1. 출력용 자료 갱신
        maxDistance[0] = Math.max(count, maxDistance[0]);

        // 2. 가지치기
        for (int[] direction : directions) {
            int nextX = x + direction[0];
            int nextY = y + direction[1];
            if (nextX < 0 || nextX >= map.length || nextY < 0 || nextY >= map[0].length) { continue; }

            // 방문하지 않은 알파벳에 한해 진행
            char nextChar = map[nextX][nextY];
            if (!visitedAlphabet.contains(nextChar)) {
                // 3.1 갱신
                visitedAlphabet.add(nextChar);

                // 3.2 진행
                dfsFunc(nextX, nextY, count+1, map, directions, visitedAlphabet, maxDistance);

                // 3.3 복구
                visitedAlphabet.remove(nextChar);
            }
        }
    }


    void bfs () {
        // 문제 예시: [0, 0] 에서 [4, 5]로 이동하는 최단거리 출력, 경로를 출력하시오.
        int[][] map = {
                {0, 1, 1, 0, 0},
                {0, 0, 1, 0, 1},
                {0, 0, 0, 0, 0},
                {1, 0, 1, 1, 0}
        };
        System.out.println("============= BFS ==============");
        System.out.println("입력 :");
        for (int[] row : map) {
            System.out.println(Arrays.toString(row));
        }

        // 사전 자료 구조
        record Node (int x, int y) {}
        int[][] directions = new int[][] { {0, -1}, {0, 1}, {-1, 0}, {1, 0} };

        // 1. 필요한 자료 구조 정리
        Deque<Node> nodeDeque = new ArrayDeque<>();                         // 방문 순서를 보관할 큐
        boolean[][] visited = new boolean[map.length][map[0].length];       // 방문 여부를 보관할 배열
        int[][] distance = new int[map.length][map[0].length];              // (최단거리 출력용) 시작점으로부터 거리를 보관할 배열
        Node[][] before = new Node[map.length][map[0].length];              // (경로 출력용) 방문 전 지점을 보관할 배열

        // 2. 시작 지점 처리
        nodeDeque.offer(new Node(0, 0));
        visited[0][0] = true;
        distance[0][0] = 0;
        before[0][0] = null;

        // 3. 큐에서 다음 지점이 빌 때까지 반복
        while (!nodeDeque.isEmpty()) {
            // 큐에서 dequeue함
            Node currentNode = nodeDeque.poll();

            // 종료 지점이면 중단
            if (currentNode.x == map.length-1 && currentNode.y == map[0].length) {
                break;
            }

            // 가지치기
            for (int[] direction : directions) {
                int nextX = currentNode.x + direction[0];
                int nextY = currentNode.y + direction[1];
                if (nextX < 0 || nextX >= map.length || nextY < 0 || nextY >= map[0].length) { continue; }

                // 방문하지 않은 지점에 한해 다음 진행 노드로 enqueue 및 처리
                if (!visited[nextX][nextY]) {
                    nodeDeque.offer(new Node(nextX, nextY));
                    visited[nextX][nextY] = true;
                    distance[nextX][nextY] = distance[currentNode.x][currentNode.y] + 1;
                    before[nextX][nextY] = currentNode;
                }
            }
        }

        // 출력
        System.out.println("출력 :");
        System.out.println(" - 도착거리");
        for (int[] distanceRow : distance) {
            System.out.println(Arrays.toString(distanceRow));
        }
        System.out.println(" - 경로(역순)");
        System.out.print("[%d, %d] ".formatted(before.length-1, before[0].length-1));
        Node visitedNode = before[before.length-1][before[0].length-1];
        while (visitedNode != null) {
            System.out.print("[%d, %d] ".formatted(visitedNode.x, visitedNode.y));
            visitedNode = before[visitedNode.x][visitedNode.y];
        }
        System.out.println();

    }

}
