package org.ocpteam.ocp_agent;

import static org.junit.Assert.assertTrue;

import org.ocpteam.misc.JLG;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
	@org.junit.Test
    public void testApp()
    {
    	JLG.debug_on();
    	JLG.debug("coucou");
        assertTrue( true );
    }
}
