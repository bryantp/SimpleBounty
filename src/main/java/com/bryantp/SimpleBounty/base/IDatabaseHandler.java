package com.bryantp.SimpleBounty.base;

import java.util.Map;

import com.bryantp.SimpleBounty.PlayerProfile;

public interface IDatabaseHandler {
	
	void setupConnection();
		
	void push(Map<String,PlayerProfile> data);
	
	Map<String, PlayerProfile> pull();
	
	void closeConnection();

}
