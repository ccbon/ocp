package org.ocpteam.interfaces;

import java.io.Serializable;

/**
 * A domain specify for a given contact the responsibility area for storing
 * content. Depending of the rules to split the universe, the function are
 * specifcally defined. For instance CAN and Chord DHT have very different way
 * of specifying the responsability area.
 * 
 */
public interface IDomain extends Serializable {

}
