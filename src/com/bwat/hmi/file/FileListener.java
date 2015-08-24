package com.bwat.hmi.file;

import java.io.File;

public interface FileListener {
	void fileCreated( File f );
	
	void fileChanged( File f );
	
	void fileDeleted( File f );
}
