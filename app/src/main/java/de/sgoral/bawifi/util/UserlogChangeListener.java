package de.sgoral.bawifi.util;

/**
 * Notifies of changes to the userlog.
 */
public abstract class UserlogChangeListener {

    /**
     * The userlog was loaded from the filesystem.
     */
    public void onUserlogLoaded() {
    }

    /**
     * The userlog was saved to the filesystem.
     */
    public void onUserlogSaved() {
    }

    /**
     * An entry was added to the userlog.
     *
     * @param entry The entry that was added.
     */
    public void onEntryAdded(UserlogEntry entry) {
    }

    /**
     * The userlog was cleared.
     */
    public void onUserlogCleared() {
    }

}
