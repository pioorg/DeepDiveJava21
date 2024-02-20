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
package org.przybyl.ddj21.concurrency.virtThreads;

public class VirtThreads {

    public static void main(String[] args) throws Exception {
        Thread.ofVirtual().start(() -> {
            Thread ct = Thread.currentThread();
            System.out.printf("Is current thread virtual? %b%n", ct.isVirtual());
            System.out.printf("Current thread's ID: %d%n", ct.threadId());
            System.out.printf("Current thread's deprecated ID: %d%n", ct.getId());
            System.out.printf("Is current thread a daemon? %b%n", ct.isDaemon());
            System.out.println();

        }).join();

        if (args.length > 0) {
            getBooksFromElasticsearch(args[0], "Java", 20);
        }
    }

    public static void getBooksFromElasticsearch(String uri, String query, int times) throws Exception {
        try (
            var transport = ClientHelper.createTransport(uri);
            var hlClient = ClientHelper.createHighLevelRestClient(uri)
        ) {
            var booksSearcher = new BooksSearcher(hlClient, transport);
            Thread[] threads = new Thread[times];
            for (int i = 0; i < times; i++) {
                final var c = i;
                threads[i] = Thread.startVirtualThread(() -> booksSearcher.searchBooksWithHighLevelRestClient(query, c));
//                threads[i] = Thread.startVirtualThread(() -> booksSearcher.searchBooksWithEsClient(query, c));
                System.out.print(("[ Created " + c + "] "));
            }
            System.out.println();
            for (Thread t : threads) {
                t.join();
            }
        }
    }

}

