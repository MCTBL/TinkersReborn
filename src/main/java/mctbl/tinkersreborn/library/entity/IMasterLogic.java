package mctbl.tinkersreborn.library.entity;

public interface IMasterLogic {

    /**
     * Called when servants change their state
     * 
     * @param x Servant X
     * @param y Servant Y
     * @param z Servant Z
     */
    public void notifyChange(IServantLogic servant, int x, int y, int z);

    /**
     * check the whole structure
     */
    public abstract void checkWholeStructureValid();

    public default void tickPre() {}

    public default void tickPost() {}

    /**
     * check one block inside of structure
     */
    public default void checkSteppingingValid() {}

    /**
     * steep to next inner block need to check valid
     */
    public default void stepNextInnerPos() {}

}
