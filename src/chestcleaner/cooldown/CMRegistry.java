package chestcleaner.cooldown;

import chestcleaner.utils.messages.enums.MessageID;

import java.util.HashMap;
import java.util.Map;

/**
 * This class organizes the CooldownManager. It is used to make multiple CooldownManger instances handy to use.
 */
public class CMRegistry {

    private static CMRegistry instance;
    private Map<CMIdentifier, CooldownManager> cmMap;

    protected CMRegistry() {
        cmMap = new HashMap<>();
        register(CMIdentifier.SORTING, new PlayerCM(MessageID.ERROR_YOU_COOLDOWN_SORTING, CMIdentifier.SORTING));
        register(CMIdentifier.CLEANING_ITEM_GET, new PlayerCM(MessageID.ERROR_YOU_COOLDOWN_GENERIC, CMIdentifier.CLEANING_ITEM_GET));
    }

    /**
     * Registers a {@code CooldownManager} with an associated {@code CMIdentifier}.
     *
     * @param id      the id which you want to associate with the manager.
     * @param manager the manager which you want to associate with the id.
     */
    public void register(CMIdentifier id, CooldownManager manager) {
        cmMap.put(id, manager);
    }

    private boolean isObjOnCooldown(CMIdentifier id, Object obj) {
        CooldownManager manger = cmMap.get(id);
        if (manger == null) {
            return false;
        } else {
            return manger.isOnCooldown(obj);
        }
    }

    /**
     * Returns the instance of this singleton class.
     *
     * @return the instance of this singleton class.
     */
    public static CMRegistry getInstance() {
        if (instance == null) {
            instance = new CMRegistry();
        }
        return instance;
    }

    /**
     * Returns the result of the associated {@code CooldownManager}. If the manager is null it returns false.
     *
     * @param id  the id associated with the {@code CooldownManager}.
     * @param obj the object the method hands over to the isOnCooldown(Object) method of the {@code CooldownManager}.
     * @return the result of the isOnCooldown(Object) method of the {@code CooldownManager} or false if the manager is null.
     */
    public static boolean isOnCooldown(CMIdentifier id, Object obj) {
        return getInstance().isObjOnCooldown(id, obj);
    }

    public enum CMIdentifier {
        SORTING, CLEANING_ITEM_GET
    }

}
