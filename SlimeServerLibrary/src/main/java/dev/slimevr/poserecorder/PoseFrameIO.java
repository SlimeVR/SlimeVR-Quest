package dev.slimevr.poserecorder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.processor.TrackerBodyPosition;

public final class PoseFrameIO {
	
	private PoseFrameIO() {
		// Do not allow instantiating
	}
	
	public static boolean writeFrames(DataOutputStream outputStream, PoseFrame frames) {
		try {
			if(frames != null) {
				outputStream.writeInt(frames.getTrackerCount());
				for(PoseFrameTracker tracker : frames.getTrackers()) {
					
					outputStream.writeUTF(tracker.name);
					outputStream.writeInt(tracker.getFrameCount());
					for(int i = 0; i < tracker.getFrameCount(); i++) {
						TrackerFrame trackerFrame = tracker.safeGetFrame(i);
						if(trackerFrame == null) {
							outputStream.writeInt(0);
							continue;
						}
						
						outputStream.writeInt(trackerFrame.getDataFlags());
						
						if(trackerFrame.hasData(TrackerFrameData.DESIGNATION)) {
							outputStream.writeUTF(trackerFrame.designation.designation);
						}
						
						if(trackerFrame.hasData(TrackerFrameData.ROTATION)) {
							outputStream.writeFloat(trackerFrame.rotation.getX());
							outputStream.writeFloat(trackerFrame.rotation.getY());
							outputStream.writeFloat(trackerFrame.rotation.getZ());
							outputStream.writeFloat(trackerFrame.rotation.getW());
						}
						
						if(trackerFrame.hasData(TrackerFrameData.POSITION)) {
							outputStream.writeFloat(trackerFrame.position.getX());
							outputStream.writeFloat(trackerFrame.position.getY());
							outputStream.writeFloat(trackerFrame.position.getZ());
						}
					}
				}
			} else {
				outputStream.writeInt(0);
			}
		} catch(Exception e) {
			LogManager.log.severe("Error writing frame to stream", e);
			return false;
		}
		
		return true;
	}
	
	public static boolean writeToFile(File file, PoseFrame frames) {
		try(DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			writeFrames(outputStream, frames);
		} catch(Exception e) {
			LogManager.log.severe("Error writing frames to file", e);
			return false;
		}
		
		return true;
	}
	
	public static PoseFrame readFrames(DataInputStream inputStream) {
		try {
			
			int trackerCount = inputStream.readInt();
			FastList<PoseFrameTracker> trackers = new FastList<PoseFrameTracker>(trackerCount);
			for(int i = 0; i < trackerCount; i++) {
				
				String name = inputStream.readUTF();
				int trackerFrameCount = inputStream.readInt();
				FastList<TrackerFrame> trackerFrames = new FastList<TrackerFrame>(trackerFrameCount);
				for(int j = 0; j < trackerFrameCount; j++) {
					int dataFlags = inputStream.readInt();
					
					TrackerBodyPosition designation = null;
					if(TrackerFrameData.DESIGNATION.check(dataFlags)) {
						designation = TrackerBodyPosition.getByDesignation(inputStream.readUTF());
					}
					
					Quaternion rotation = null;
					if(TrackerFrameData.ROTATION.check(dataFlags)) {
						float quatX = inputStream.readFloat();
						float quatY = inputStream.readFloat();
						float quatZ = inputStream.readFloat();
						float quatW = inputStream.readFloat();
						rotation = new Quaternion(quatX, quatY, quatZ, quatW);
					}
					
					Vector3f position = null;
					if(TrackerFrameData.POSITION.check(dataFlags)) {
						float posX = inputStream.readFloat();
						float posY = inputStream.readFloat();
						float posZ = inputStream.readFloat();
						position = new Vector3f(posX, posY, posZ);
					}
					
					trackerFrames.add(new TrackerFrame(designation, rotation, position));
				}
				
				trackers.add(new PoseFrameTracker(name, trackerFrames));
			}
			
			return new PoseFrame(trackers);
		} catch(Exception e) {
			LogManager.log.severe("Error reading frame from stream", e);
		}
		
		return null;
	}
	
	public static PoseFrame readFromFile(File file) {
		try {
			return readFrames(new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
		} catch(Exception e) {
			LogManager.log.severe("Error reading frame from file", e);
		}
		
		return null;
	}
}
