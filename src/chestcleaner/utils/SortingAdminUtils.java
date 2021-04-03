package chestcleaner.utils;

import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortingAdminUtils {

    public static Sound getSoundByName(String name){
        List<Sound> soundList = Arrays.stream(Sound.values()).
                filter(s -> s.toString().equalsIgnoreCase(name)).collect(Collectors.toList());
        if(soundList.size() > 0) return soundList.get(0);
        return null;
    }

}
