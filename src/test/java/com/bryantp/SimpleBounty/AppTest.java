package com.bryantp.SimpleBounty;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;


/**
 * Unit test for simple App.
 */
public class AppTest{
	
	
	@BeforeClass
	public static void setupTest(){
		
	}
	
	
	@Test
    public void testOnDeath(){
		//Create two mock players
		Player killer = mock(Player.class);
		Player victim = mock(Player.class);
		
		//Set their names
		when(killer.getName()).thenReturn("Killer");
		when(victim.getName()).thenReturn("Vicitm");
		
		//Create the bountListener and a Entity Death Event
		BountyListener bountyListener = mock(BountyListener.class);
		EntityDeathEvent deathEvent = mock(EntityDeathEvent.class);
		
		when(deathEvent.getEntity()).thenReturn(victim); //Return the victim player object
		EntityDamageByEntityEvent entityDamageEvent = mock(EntityDamageByEntityEvent.class); 
		when(entityDamageEvent.getDamager()).thenReturn(killer); //The person who damaged the victim is the killer. 
		
		
		bountyListener.onDeath(deathEvent);
		
		verify(victim, never()).isDead();
		
		
		
    }
}
