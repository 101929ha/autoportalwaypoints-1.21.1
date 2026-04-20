package com._101929ha.autoportalwaypoints;
//TODO set up a waypoint group 
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent; //PlayerChangedDimensionEvent in PlayerEvent
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import java.util.ArrayList;
import java.util.List;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import journeymap.api.v2.common.waypoint.WaypointGroup;
import com.lightning.northstar.world.dimension.NorthstarDimensions;


@journeymap.api.v2.common.JourneyMapPlugin(apiVersion = "2.0.0")
// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = AutoPortalWaypoints.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = AutoPortalWaypoints.MODID, value = Dist.CLIENT)
public class AutoPortalWaypointsClient implements IClientPlugin{
	private static IClientAPI jmAPI = null;
	/** //From example mod:
	private static AutoPortalWaypoints INSTANCE;
    public AutoPortalWaypoints() {
        INSTANCE = this;
    }
    public static AutoPortalWaypoints getInstance() {
        return INSTANCE;
    }
	*/
	
	static ResourceKey<Level> entryDim;
	//static BlockPos entryBlockPos;
	static ResourceKey<Level> destinationDim;
	//static BlockPos destinationBlockPos;
	static boolean waitingForNextTick = false;
	static boolean waitingForChunkLoad = false;
	static int tickPause = 0;
	static Level level;
	static List<ResourceKey<Level>> exclusions = new ArrayList<>(); //For any dimension that doesn't use the typical portal/return portal
	static List<ResourceKey<Level>> exclusionsDestinationOnly = new ArrayList<>(); //Orbit doesn't have a surface, so this will disable the waypoint algorithm so it doesn't try to find land all the time
	static List<ResourceKey<Level>> planets = new ArrayList<>(); //For dimensions that are accessed by falling from the sky
	

	static WaypointGroup waypointgroup = WaypointFactory.createWaypointGroup(AutoPortalWaypoints.MODID, "Portals"); //FIXME
    //public AutoPortalWaypointsClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        //container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        //NeoForge.EVENT_BUS.addListener(AutoPortalWaypoints::changedDimensions);
    	//this.initialize(jmAPI);
    //}
    /**
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        AutoPortalWaypoints.LOGGER.info("HELLO FROM CLIENT SETUP");
        AutoPortalWaypoints.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }*/
    
    @SubscribeEvent
    public static void changedDimensions(PlayerChangedDimensionEvent event)
    {
    	AutoPortalWaypoints.LOGGER.info("Name: "+ waypointgroup.getName());
    	AutoPortalWaypoints.LOGGER.info("Mod ID: "+ waypointgroup.getModId());
    	if (exclusions.isEmpty()) { //exclusions should always contain Level.END. If it doesn't, then this is the first time changing dimensions. Please don't change dependency mods after changing dimensions.
    		exclusions.add(Level.END);
            AutoPortalWaypoints.LOGGER.info("exclusions: "+exclusions.isEmpty());
            if (ModList.get().isLoaded("northstar")) {
            	exclusionsDestinationOnly.add(NorthstarDimensions.EARTH_ORBIT_DIM_KEY);
            	planets.add(NorthstarDimensions.MARS_DIM_KEY);
            	planets.add(NorthstarDimensions.MERCURY_DIM_KEY);
            	planets.add(NorthstarDimensions.MOON_DIM_KEY); //Yes, I know that the moon isn't a planet. This is just revenge for astrophysicists calling all post-Helium elements 'metals'.
            	planets.add(NorthstarDimensions.VENUS_DIM_KEY);
        	}
            AutoPortalWaypoints.LOGGER.info("exclusionsDestinationOnly: "+exclusionsDestinationOnly.isEmpty());
            AutoPortalWaypoints.LOGGER.info("planets: "+planets.isEmpty());
    	}
    	//Minecraft.getInstance().player.portalProcess.getEntryPosition();
    	//EntityTravelToDimensionEvent
    	//AutoPortalWaypoints.LOGGER.info("Entry pos: "+Minecraft.getInstance().player.portalProcess.getEntryPosition());
    	
    	//BlockPos pos = Minecraft.getInstance().player.getRelativePortalPosition(null, null)
    	//Minecraft.getInstance().player.blockPosition()
    	//ModID, BlockPos, str PrimaryDimension
    	//Waypoint portalWaypoint = null;
    	//IClientAPI jmAPI = null;
    	/**
    	Codec.
    	public portal portal;
    	public "Minecraft.getInstance().player.portalProcess" portal;
    	Portal e = Minecraft.getInstance().player.portalProcess.portal;
    	event.getLookupProvider();
    	Minecraft.getInstance().player.portalProcess.portal;
    	//Minecraft.getInstance().player.portalProcess.getPortalDestination(null, null);
    	Minecraft.getInstance().player.portalProcess;
    	*/
    	//portalWaypoint = new Waypoint(AutoPortalWaypoints.MODID);
    	//WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, Minecraft.getInstance().player.portalProcess.getEntryPosition(), "portal" ,event.getFrom(), true);
    	/**
    	if (!isDuplicateWaypoint(Minecraft.getInstance().player.portalProcess.getEntryPosition(), event.getFrom())) {
    		jmAPI.addWaypoint(AutoPortalWaypoints.MODID, WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, Minecraft.getInstance().player.portalProcess.getEntryPosition(), "Portal" ,event.getFrom(), true)); //Works on entrance portal
    		
    		//jmAPI.addWaypoint(AutoPortalWaypoints.MODID, WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, event.toDim, "portal" ,event.getFrom(), true));
    	}*/
    	destinationDim = event.getTo();
    	entryDim = event.getFrom();
    	//Level end = minecraft:the_end>;
    	//ResourceKey<Level> end = ResourceKey<minecraft:the_end>;
    	
    	if (exclusions.contains(destinationDim) || exclusions.contains(entryDim) || exclusionsDestinationOnly.contains(destinationDim)){
    		AutoPortalWaypoints.LOGGER.info("This dimension change is ineligible for a portal");
    		return; //don't wait for dimension to load, therefore don't do anything when entering/exiting the End or when entering orbit
    	}
    	//ResourceKey<Level> ertvsdf = NorthstarDimensions.EARTH_ORBIT_DIM_KEY;
    	
    	
    	/**
    	if (!isDuplicateWaypoint(entryBlockPos, event.getTo())) {
    		jmAPI.addWaypoint(AutoPortalWaypoints.MODID, WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, entryBlockPos, "Portal" ,event.getTo(), true)); //For destination portal
    	}*/
    	waitingForNextTick = true;
    	//tickPause = 80; //Wait 80 ticks to ensure dimension change works
    }
    
    /**
    @SubscribeEvent
    public static void dimensionsChanged(EntityTravelToDimensionEvent event) {
    	entryBlockPos = event.getEntity().blockPosition();
    }*/
    
    public static boolean isDuplicateWaypoint(BlockPos coord, ResourceKey<Level> dim) {
    	List<? extends Waypoint> waypoints = jmAPI.getAllWaypoints(dim); //check all waypoints in this dimension
    	for (int i = 0; i < waypoints.size(); i++) {
    		Waypoint waypoint = waypoints.get(i);
    		if (Config.DUPLICATE_PROXIMITY.get().equals(-1.0)) {
    			return true; // simplest way to turn this mod off :)
    		}
    		if (waypoint.getBlockPos().closerThan(coord, Config.DUPLICATE_PROXIMITY.get())){ //if it is within a cube of radius 3, consider it a duplicate
    			return true; //a waypoint is too close, it is probably a duplicate
    		}
    	}
    	return false; //none of the existing waypoints are too close
    }
    /**
    @SubscribeEvent
    public static void dimLoaded(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
    	if (waitingForNextTick) {
    		if (!isDuplicateWaypoint(Minecraft.getInstance().player.blockPosition(), destinationDim)) {
        		jmAPI.addWaypoint(AutoPortalWaypoints.MODID, WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, Minecraft.getInstance().player.blockPosition(), "Portal" , destinationDim, true)); //For destination portal
        	}
        	waitingForNextTick = false;
    	}
    }*/
    
    @SubscribeEvent
    public static void newTick(PlayerTickEvent.Post event) {
    	
    	
    	if (waitingForNextTick && Minecraft.getInstance().player != null) { //Player has changed dimensions, now we need to wait for the dimension to load properly.
    		if (Minecraft.getInstance().player.level().dimension().equals(destinationDim)) { //Teleportation complete. Very important.
    			
    			//level#getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos)
    			//tickPause = 80;
    			
    			if (planets.contains(destinationDim) || planets.contains(entryDim)){ //Spacecraft fall from y=1750, so we need to handle these differently
    				if (waitingForChunkLoad == false) { //Only want to print this once, rather than every tick :)
    					AutoPortalWaypoints.LOGGER.info("Entering or exiting a Northstar dimension. Waiting for chunk to load before placing waypoint at surface level.");    					
    				}
    				waitingForChunkLoad = true;
    				/**try { //I'm not sure how space stations will be implemented, this should ensure no problems from getHeightmapPos
    					BlockPos topBlock = new BlockPos(null);
    	    	    	//Level level = Minecraft.getInstance().player.level();
    	    	    	topBlock = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Minecraft.getInstance().player.blockPosition());
    	    	    	
    	    	    	jmAPI.addWaypoint(AutoPortalWaypoints.MODID, WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, topBlock, "Spaceship" , destinationDim, true));
    	    	    	AutoPortalWaypoints.LOGGER.info("Spaceship marked at " + topBlock + " in " + destinationDim);
    				} catch (Exception e) {
    					AutoPortalWaypoints.LOGGER.error("Failed to mark spaceship. The terrain height probably couldn't be found at " + Minecraft.getInstance().player.blockPosition() + " in " + destinationDim);
    				}*/
    				
    				return; //we'll handle the waypoint creation in waitingForChunkLoad
    			}
    			
    			
    			if (!isDuplicateWaypoint(Minecraft.getInstance().player.blockPosition(), destinationDim)) { //Make sure there isn't already a waypoint there
    				//jmAPI.addWaypoint(AutoPortalWaypoints.MODID, WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, Minecraft.getInstance().player.blockPosition(), "Portal" , destinationDim, true)); //Make waypoint at destination portal
    				waypointgroup.addWaypoint(WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, Minecraft.getInstance().player.blockPosition(), "Portal" , destinationDim, true)); //FIXME
    				AutoPortalWaypoints.LOGGER.info("Portal marked at " + Minecraft.getInstance().player.blockPosition() + " in " + destinationDim);
    			} else {
    				AutoPortalWaypoints.LOGGER.info("Attempted to mark portal at " + Minecraft.getInstance().player.blockPosition() + " in " + destinationDim + ", but another waypoint is too close");
    			}
    			waitingForNextTick = false;
    		}
    	}
    	
    	if (tickPause > 0) {
    		tickPause--;
    		//Minecraft.getInstance().player.level().dimension();
    		//AutoPortalWaypoints.LOGGER.info("Player location:"+Minecraft.getInstance().player.blockPosition());
    		//AutoPortalWaypoints.LOGGER.info("Player dimension:"+Minecraft.getInstance().player.level().dimension());
    		//Minecraft.getInstance().player.chunkPosition().loa
    		/**
    		AutoPortalWaypoints.LOGGER.info(""+Minecraft.getInstance().player.touchingUnloadedChunk());
    		if(Minecraft.getInstance().player.touchingUnloadedChunk() == false) {
    			AutoPortalWaypoints.LOGGER.info(""+ level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(Minecraft.getInstance().player.blockPosition().getX(),0,Minecraft.getInstance().player.blockPosition().getZ())));
    		}*/
    		/**if (Minecraft.getInstance().player.level().dimension().equals(destinationDim)) { //Teleportation complete. Very important.
    			AutoPortalWaypoints.LOGGER.info(""+Minecraft.getInstance().player.touchingUnloadedChunk());
    		}*/
    		/**if (tickPause == 0) {
    			//jmAPI.addWaypoint(AutoPortalWaypoints.MODID, WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, Minecraft.getInstance().player.blockPosition(), "Portal" , destinationDim, true)); //For destination portal
    			AutoPortalWaypoints.LOGGER.info(""+ level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(Minecraft.getInstance().player.blockPosition().getX(),0,Minecraft.getInstance().player.blockPosition().getZ())));
    			AutoPortalWaypoints.LOGGER.info(""+ level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(Minecraft.getInstance().player.blockPosition().getX(),0,Minecraft.getInstance().player.blockPosition().getZ())));
    			AutoPortalWaypoints.LOGGER.info(""+ level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(Minecraft.getInstance().player.blockPosition().getX(),0,Minecraft.getInstance().player.blockPosition().getZ())));
    			AutoPortalWaypoints.LOGGER.info(""+ level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(Minecraft.getInstance().player.blockPosition().getX(),0,Minecraft.getInstance().player.blockPosition().getZ())));
    		}*/
    	}
    }
    @SubscribeEvent
    public static void chunkLoad(ChunkEvent.Load event) {
    	if(waitingForChunkLoad && Minecraft.getInstance().player != null) {
    		level = Minecraft.getInstance().player.level();
    		if(level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()).getY() != level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Minecraft.getInstance().player.blockPosition()).getY()) { //MOTION_BLOCKING_NO_LEAVES seems to be bugged and always shows the lowest y coord, so this tells me if the chunk is loaded
    			waitingForChunkLoad = false;
    			waitingForNextTick = false;
    			AutoPortalWaypoints.LOGGER.info("Chunk loaded, y="+level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()).getY()+" vs y= "+level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Minecraft.getInstance().player.blockPosition()).getY());
    			if (!isDuplicateWaypoint(level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()), destinationDim)) { //Make sure there isn't already a waypoint there
    				//jmAPI.addWaypoint(AutoPortalWaypoints.MODID, WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()), "Spaceship" , destinationDim, true)); //Make waypoint at destination portal
    				waypointgroup.addWaypoint(WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()), "Spaceship" , destinationDim, true)); //FIXME
    				AutoPortalWaypoints.LOGGER.info("Spaceship marked at " + level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()) + " in " + destinationDim);
    			} else {
    				AutoPortalWaypoints.LOGGER.info("Attempted to mark spaceship at " + level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()) + " in " + destinationDim + ", but another waypoint is too close");
    			}
    		}
    		//AutoPortalWaypoints.LOGGER.info(""+ level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(Minecraft.getInstance().player.blockPosition().getX(),0,Minecraft.getInstance().player.blockPosition().getZ())));
			//AutoPortalWaypoints.LOGGER.info(""+ level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(Minecraft.getInstance().player.blockPosition().getX(),0,Minecraft.getInstance().player.blockPosition().getZ())));
			//AutoPortalWaypoints.LOGGER.info(""+ level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(Minecraft.getInstance().player.blockPosition().getX(),0,Minecraft.getInstance().player.blockPosition().getZ())));
			//AutoPortalWaypoints.LOGGER.info(""+ level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(Minecraft.getInstance().player.blockPosition().getX(),0,Minecraft.getInstance().player.blockPosition().getZ())));
    		
    	}
    }
    
    
    /**@SubscribeEvent
    //public static void mappingStageEvent(MappingEvent.Stage event) {
    public static void clientEvent(ClientEvent event) {
    	AutoPortalWaypoints.LOGGER.info("JM triggered");
    }*/
    
    /**
    @SubscribeEvent
    //public static void postTransition(PostDimensionTransition event) {
    public static void postTransition(firePlayerChangedDimensionEvent event) {
    	if (!isDuplicateWaypoint(Minecraft.getInstance().player.blockPosition(), destinationDim)) {
    		jmAPI.addWaypoint(AutoPortalWaypoints.MODID, WaypointFactory.createClientWaypoint(AutoPortalWaypoints.MODID, Minecraft.getInstance().player.blockPosition(), "Portal", destinationDim, true));
    	}
    }*/

	@Override
	public String getModId() {
		return AutoPortalWaypoints.MODID;
	}

	@Override
	public void initialize(final IClientAPI jmClientApi) {
		this.jmAPI = jmClientApi;
		jmAPI.addWaypointGroup(waypointgroup); //FIXME
		
        //waypointgroup.setName("Portals");
        //waypointgroup.setPersistent(true);
        AutoPortalWaypoints.LOGGER.info("Initialized " + getClass().getName());
        
        
        //NeoForge.EVENT_BUS.register(this);
        
        //ClientEventRegistry.MAPPING_EVENT.subscribe(AutoPortalWaypoints.MODID, this::mappingStageEvent);
        
	}
    
}
