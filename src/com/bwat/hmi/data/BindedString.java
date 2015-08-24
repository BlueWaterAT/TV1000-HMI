package com.bwat.hmi.data;

import java.io.File;
import java.nio.file.Files;

import com.bwat.hmi.file.FileListener;
import com.bwat.hmi.file.FileWatcher;
import com.bwat.hmi.util.FileUtils;

/**
 * A String that is binded to a file's contents. This will be updated as the file is updated.
 * 
 * @author Kareem El-Faramawi
 */
public class BindedString {
	private String content;
	private BindedStringListener listener = null;
	private File bindedFile = null;
	
	public BindedString( File src, BindedStringListener listener ) {
		this( src.getPath(), listener );
	}
	
	public BindedString( String srcPath, BindedStringListener listener ) {
		// Set initial content
		bindedFile = FileUtils.getFile( srcPath );
		setContent( bindedFile.exists() && bindedFile.isFile() ? FileUtils.readToString( bindedFile ) : "" );
		
		// Listen for changes to the file and update the content
		this.listener = listener;
		FileWatcher.watchFile( srcPath, new FileListener() {
			@Override
			public void fileDeleted( File f ) {
				setContent( "" );
			}
			
			@Override
			public void fileCreated( File f ) {
				fileChanged( f );
			}
			
			@Override
			public void fileChanged( File f ) {
				String c = FileUtils.readToString( f );
				if ( !c.equals( content ) ) {
					setContent( FileUtils.readToString( f ) );
				}
			}
		} );
	}
	
	/**
	 * Save a new value to the file
	 * 
	 * @param content New String to save
	 */
	public void setContent( String content ) {
		if ( !content.equals( this.content ) ) {
			this.content = content;
			if ( listener != null ) {
				listener.stringChanged( this.content );
			}
			FileUtils.writeStringAsFile( this.content, bindedFile );
		}
	}
	
	public String getContent() {
		return content;
	}
}