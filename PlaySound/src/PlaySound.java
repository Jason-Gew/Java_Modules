
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * @author Jason/Ge Wu
 * @since 2017-10-09
 */
public class PlaySound
{
    private static String soundPath;

    public static String getSoundPath() { return soundPath; }
    public static void setSoundPath(final String soundPath)
    {
        PlaySound.soundPath = soundPath;
        File path = new File(soundPath);
        if(!path.exists())
            throw new IllegalArgumentException("File Does Not Exist");
    }

    private static PlaySound instance;

    public static PlaySound getInstance()
    {
        if(instance == null)
        {
            synchronized (PlaySound.class)
            {
                if (instance == null) {
                    instance = new PlaySound(soundPath);
                }
            }
        }
        return instance;
    }

    private PlaySound(String sound)
    {
        if(soundPath == null)
            throw new IllegalArgumentException("Invalid Sound Path, Invoke setSoundPath First");
    }

    public void play(final String path)
    {
        File sound;
        try {
            sound = new File(path).getAbsoluteFile();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("-> Play Sound @ " + path + "Failed: " + ex.getMessage());
        }
    }

    public void play(String... paths)
    {

        if(paths.length == 0)
        {
            play(soundPath);
        }
        for(String path : paths)
        {
            play(path);
        }
    }
}
