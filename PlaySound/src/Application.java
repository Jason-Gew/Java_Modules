
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application
{
    private static int threadNum = 3;

    public static void main(String[] args)
    {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        for(int i = 0; i < 3; i++) {
            executorService.execute(new SoundPlay());
            System.out.println("---> Playing Sound: " + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        executorService.shutdown();
        System.out.println(executorService.isShutdown());

        PlaySound.setSoundPath("resources/bleep.wav");
        PlaySound sound = PlaySound.getInstance();

        sound.play();
    }


}
