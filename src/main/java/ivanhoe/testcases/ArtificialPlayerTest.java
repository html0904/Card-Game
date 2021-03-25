package ivanhoe.testcases;

import ivanhoe.client.skynet.ArtificialPlayer;
import ivanhoe.client.skynet.JimTheDestroyer;
import org.junit.Test;

/**
 * Created by lee on 3/15/2016.
 */
public class ArtificialPlayerTest {

    /**
     * Run this test after the server is started and watch the magic happen
     *
     * @throws Exception
     */
    @Test
    public void testEnterNewGame() throws Exception {

        ArtificialPlayer ai = new JimTheDestroyer(8080, "127.0.0.1");
        ai.enterNewGame();
    }
//    @Test
//    public void excecutorExample() {
//
//        //this is now a threadpool
//        ExecutorService exec = Executors.newCachedThreadPool();
//        Random rand = new Random();
//        //i will now run 5 threads with randomized sleep times
//
//        for (int i = 0; i < 5; i++) {
//            exec.execute(
//
//                    // this is a lambda
//                    () -> {
//                        try {
//                            Thread.sleep(rand.nextInt(3000));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println(Thread.currentThread().toString() + " just woke up");
//                    }
//            );
//
//        }
//        exec.shutdown();
//        try {
//            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}