
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundPlay implements Runnable
{
    private static String soundName = "resources/new_msg.wav";
    private String dynamicSound;

    public static String getSoundName() { return soundName; }
    public static void setSoundName(final String soundName) { SoundPlay.soundName = soundName; }

    public SoundPlay() { }

    public SoundPlay(final String dynamicSound) { this.dynamicSound = dynamicSound; }

    @Override
    public void run()
    {
        File sound;
        try{
            if(dynamicSound != null && !dynamicSound.isEmpty())
            {
                sound = new File(dynamicSound).getAbsoluteFile();
            }
            else
            {
                sound = new File(soundName).getAbsoluteFile();
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength()/1000);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.err.println("-> Sound Play Failed: " + ex.getMessage());
        }
    }
}
