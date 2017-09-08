package me.johz.infinitic.client;

import java.util.ArrayList;

import java.util.List;

import me.johz.infinitic.lib.helpers.ClipboardHelper;
import me.johz.infinitic.lib.helpers.GenericHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;

/**
 * Bulk of this class was taken from CraftTweaker where the Authors were listed as below
 * @author BloodWorkXGaming, Stan, Jared
 */
public class Command implements ICommand {

	@Override
	public int compareTo(ICommand o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return "infinitic";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/infinitic hand";
	}

	@Override
	public List<String> getCommandAliases() {
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("infini");
		aliases.add("it");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length < 1 || (!"hand".equalsIgnoreCase(args[0]))) {
			sender.addChatMessage(new TextComponentString("Need to specify 'hand'."));
			sender.addChatMessage(new TextComponentString(this.getCommandUsage(sender)));
			return;
		}
		
		if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
            // Gets player and held item
            EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
            ItemStack heldItem = player.getHeldItemMainhand();
            
            // Tries to get name of held item first
            if(heldItem != null) {
                List<String> oreDictNames = GenericHelper.getOreDictOfItem(heldItem);
                
                int meta = heldItem.getMetadata();
                String itemName = heldItem.getItem().getRegistryName() + (meta == 0 ? "" : ":" + meta);
                
                String withNBT = "";
                
                //NBT might be useful one day, but not right now
                if(heldItem.serializeNBT().hasKey("tag")) {
                    String nbt = heldItem.serializeNBT().getTag("tag").toString();
                    if(nbt.length() > 0)
                        withNBT = ".withTag(" + nbt + ")";
                }

                sender.addChatMessage(new TextComponentString("Item \u00A72" + itemName + "\u00A7a" + withNBT));
                String toCopy = "\"" + itemName + "\"";

                // adds the oredict names if it has some
                if(oreDictNames.size() > 0) {
                    sender.addChatMessage(new TextComponentString("\u00A73OreDict Entries:"));
                    for(String oreName : oreDictNames) {
                        sender.addChatMessage(new TextComponentString(" \u00A7e- \u00A7b" + oreName));
                        toCopy += ", \"ore:" + oreName + "\"";
                    }
                } else {
                    sender.addChatMessage(new TextComponentString("\u00A73No OreDict Entries"));
                }

                ClipboardHelper.copyStringPlayer(player, toCopy);
                sender.addChatMessage(new TextComponentString("Copied [\u00A76" + toCopy + "\u00A7r] to the clipboard"));
                
            } else {
                // if hand is empty, tries to get oreDict of block
	            	RayTraceResult rayTraceResult = GenericHelper.getPlayerLookat(player, 100);
                
            		if(rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos blockPos = rayTraceResult.getBlockPos();
                    IBlockState block = sender.getEntityWorld().getBlockState(blockPos);                    
                    
                    int meta = block.getBlock().getMetaFromState(block);
                    String blockName = block.getBlock().getRegistryName() + (meta == 0 ? "" : ":" + meta);
                    String toCopy = "\"" + blockName + "\"";
                    
                    sender.addChatMessage(new TextComponentString("Block \u00A72" + blockName + " \u00A7rat \u00A79[" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + "]\u00A7r"));
                    
                    // adds the oreDict names if it has some
                    try {
                        
                        List<String> oreDictNames = GenericHelper.getOreDictOfItem(new ItemStack(block.getBlock(), 1, block.getBlock().getMetaFromState(block)));
                        if(oreDictNames.size() > 0) {
                            sender.addChatMessage(new TextComponentString("\u00A73OreDict Entries:"));
                            
							for (String oreName : oreDictNames) {
								toCopy += ", \"ore:" + oreName + "\"";
								sender.addChatMessage(new TextComponentString(" \u00A7e- \u00A7b" + oreName));
							}
                        } else {
                            sender.addChatMessage(new TextComponentString("\u00A73No OreDict Entries"));
                        }
                        // catches if it couldn't create a valid ItemStack for the Block
                    } catch(IllegalArgumentException e) {
                        sender.addChatMessage(new TextComponentString("\u00A73No OreDict Entries"));
                    }
                    
                    ClipboardHelper.copyStringPlayer(player, toCopy);
                    sender.addChatMessage(new TextComponentString("Copied [\u00A76" + toCopy + "\u00A7r] to the clipboard"));
                    
                } else {
                    sender.addChatMessage(new TextComponentString("\u00A74Please hold an Item in your hand or look at a Block."));
                }
            }
        } else {
            sender.addChatMessage(new TextComponentString("This command can only be casted by a player inGame"));
        }
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if(sender instanceof EntityPlayer) {
            return true;
        }
        return false;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos pos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

}

