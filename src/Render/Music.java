package Render;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class for the music and audio for the program
 * @author Daniel Marshall
 *
 */
public class Music {

    private Map<String, Clip> audioClips;
    private final Map<String, Clip> currentlyPlaying;

    /**Constructor for the music object
     */
    public Music() {
        this.currentlyPlaying = new HashMap<>();
        try {
            List<String> audioFileNames = createAudioFileNames();
            Map<String, String> audioFilePaths = createAudioFilePaths(audioFileNames);
            Map<String, File> audioFiles = createAudioFiles(audioFilePaths);
            Map<String, AudioInputStream> audioStreams = createAudioStreams(audioFiles);
            this.audioClips = createAudioClips(audioStreams);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    /** Create a list of names of audio objects
     * @return names of audio objects
     */
    private List<String> createAudioFileNames(){
        List<String> audioFileNames = new ArrayList<>();
        audioFileNames.add("ErrorSound");
        audioFileNames.add("TimeoutSound");
        audioFileNames.add("LoadingScreenMusic");
        audioFileNames.add("LevelCompleteSound");
        audioFileNames.add("LevelMusic");
        audioFileNames.add("PickupSound");
        return audioFileNames;
    }


    /** Create a map of filepaths of audio objects
     * @return map of audio file paths
     */
    private Map<String, String> createAudioFilePaths(List<String> audioFileNames) {
        Map<String, String> audioFilePaths = new HashMap<>();
        for(String fileName: audioFileNames){
            audioFilePaths.put(fileName, "./assets/Audio/" + fileName + ".wav");
        }
        return audioFilePaths;
    }


    /** Create a map of files of audio objects
     * @return map of files
     */
    private Map<String, File> createAudioFiles(Map<String, String> fileNames){
        try {
            Map<String, File> audioFiles = new HashMap<>();
            for(Map.Entry<String, String> entry : fileNames.entrySet()){
                audioFiles.put(entry.getKey(), new File(entry.getValue()));
            }
            return audioFiles;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /** Create a map of audiostreams of audio objects
     * @return map of audiostreams
     */
    private Map<String, AudioInputStream> createAudioStreams(Map<String, File> audioFiles){
        try {
            Map<String, AudioInputStream> audioStreams = new HashMap<>();
            for(Map.Entry<String, File> entry : audioFiles.entrySet()){
                if(entry.getValue().exists()) {
                    audioStreams.put(entry.getKey(), AudioSystem.getAudioInputStream(entry.getValue()));
                }
                else{
                    System.out.println("File '" + entry.getValue().getPath() +"' does not exist");
                }
            }
            return audioStreams;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /** Create a map of Clips of audio objects
     * @return map of clips
     */
    private Map<String, Clip> createAudioClips(Map<String, AudioInputStream> audioStreams) {
        try {
            Map<String, Clip> audioClips = new HashMap<>();
            for(Map.Entry<String, AudioInputStream> entry : audioStreams.entrySet()){
                Clip clip = AudioSystem.getClip();
                clip.open(audioStreams.get(entry.getKey()));
                audioClips.put(entry.getKey(), clip);
            }
            return audioClips;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**This method is used to start playing the desired music clip
     * @param audioString - the audio to play
     */
    @SuppressWarnings("static-access")
	public void playAudio(String audioString){
        try{
            Clip clip = this.audioClips.get(audioString);
            clip.start();
            this.currentlyPlaying.put(audioString, clip);
            if(audioString.contains("Sound")){clip.loop(1);}
            else if(audioString.contains("Music")){clip.loop(clip.LOOP_CONTINUOUSLY);}
            else{throw new IllegalStateException("Cannot have an audiofile that is not sound or music");}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**This method is used to stop playing the desired music clip
     * @param audioString - the audio to stop
     */
    public void stopPlayingAudio(String audioString){
        try{
            if(audioString.equals("All")){
                for(Map.Entry<String, Clip> entry : this.currentlyPlaying.entrySet()){
                    entry.getValue().stop();
                }
                this.currentlyPlaying.clear();
            }
            else {
                if (currentlyPlaying.containsKey(audioString)) {
                    Clip clip = this.currentlyPlaying.get(audioString);
                    clip.stop();
                    this.currentlyPlaying.remove(audioString);
                } else {
                    System.out.println("Error: no clip currently playing with that name");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}