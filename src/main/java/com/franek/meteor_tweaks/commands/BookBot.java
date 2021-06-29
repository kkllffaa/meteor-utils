package com.franek.meteor_tweaks.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.MeteorClient;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.mixin.TextHandlerAccessor;
import minegame159.meteorclient.systems.commands.Command;
import minegame159.meteorclient.utils.player.FindItemResult;
import minegame159.meteorclient.utils.player.InvUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.Style;

import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.stream.IntStream;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class BookBot extends Command {
    public BookBot() {
        super("bookbot", "automaticly writes books");
    }

    private static final String DEFAULT_NAME = "meteor client";

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {

        MeteorClient.EVENT_BUS.subscribe(this);


        for (Mode mode : Mode.values()){
            if (mode == Mode.Number){
                builder.then(literal(mode.name()).then(argument("number of books", IntegerArgumentType.integer()).executes(context -> {

                    name = DEFAULT_NAME;
                    execute(context.getArgument("number of books",Integer.class));
                    return SINGLE_SUCCESS;

                }).then(argument("name",StringArgumentType.string()).executes(context -> {

                    name = context.getArgument("name",String.class);
                    execute(context.getArgument("number of books",Integer.class));
                    return SINGLE_SUCCESS;

                }))));
            }else {
                builder.then(literal(mode.name()).executes(context -> {

                    name = DEFAULT_NAME;
                    execute(mode);
                    return SINGLE_SUCCESS;

                }).then(argument("name",StringArgumentType.string()).executes(context -> {

                    name = context.getArgument("name",String.class);
                    execute(mode);
                    return SINGLE_SUCCESS;

                })));

            }
        }


        builder.then(literal("cancel").executes(context -> {

            noOfBooks = 0;
            running = false;
            from = -1;
            to = -1;
            return SINGLE_SUCCESS;
        }));




        builder.then(literal("settings").then(literal("delay").then(argument("delay (ms)",IntegerArgumentType.integer(100,10000)).executes(context -> {

            delay = context.getArgument("delay (ms)",Integer.class);

            return SINGLE_SUCCESS;
        }))).then(literal("pages").then(argument("number of pages to write",IntegerArgumentType.integer(1,100)).executes(context -> {

            noOfPages = context.getArgument("number of pages to write",Integer.class);

            return SINGLE_SUCCESS;
        }))).then(literal("dropafterwrite")
            .then(literal("yes").executes(context -> {

                drop = true;
                return SINGLE_SUCCESS;
            })).then(literal("no").executes(context -> {
                drop = false;
                return SINGLE_SUCCESS;
            }))));


    }


    public enum Mode{
        Hand,
        Hotbar,
        All,
        Number
    }



    private void inallcases(){
        IntStream charGenerator = RANDOM.ints(0x20, 0x7f);
        stream = charGenerator.limit(35000).iterator();
        from = -1;
        to = -1;
        running = true;
    }



    private void execute(Mode mode){
        this.mode = mode;
        inallcases();

    }
    private void execute(int noOfBooks){
        this.noOfBooks = noOfBooks;
        this.mode = Mode.Number;
        inallcases();

    }



    public int noOfPages = 100;

    private static final Random RANDOM = new Random();

    private final NbtList pages = new NbtList();
    private final StringBuilder pageSb = new StringBuilder();
    private final StringBuilder lineSb = new StringBuilder();

    private int nextChar;

    private static final int LINE_WIDTH = 113;

    private PrimitiveIterator.OfInt stream;

    public int noOfBooks;
    private boolean running = false;
    private int ticksLeft = 0;
    private int delay = 2000;
    private String name;
    private int from;
    private int to;
    private boolean drop = false;

    private boolean write = false;

    private Mode mode;




    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Make sure we aren't in the inventory.
        if (!running) return;
        if (mc.currentScreen != null) return;
        // If there are no books left to write we are done.
        if (mode == Mode.Number && noOfBooks <= 0){
            running = false;
            write = false;
            return;
        }
        if (mode == Mode.Hand){
            writeBook();
            write = false;
            running = false;
            return;
        }

        if (write){
            writeBook();
            write = false;

            return;
        }

        write = false;



        if (ticksLeft <= 0) {
            ticksLeft = delay;
        } else {
            ticksLeft -= 50;
            return;
        }

        if (drop) {
            assert mc.player != null;
            if (mc.player.getMainHandStack().getItem() == Items.WRITTEN_BOOK){
                InvUtils.drop().slot(mc.player.inventory.selectedSlot);
            }
            from = -1;
            to = -1;
        }



        if (from != -1 && to != -1){
            InvUtils.quickMove().from(to).to(from);
            from = -1;
            to = -1;
        }

        FindItemResult itemResult = InvUtils.find(Items.WRITABLE_BOOK);

        if (!itemResult.found()){
            info("book not found: cancelling");
            running = false;
            return;
        }



        swichslot(itemResult,mode);




    }


    private void swichslot(FindItemResult itemResult,Mode mode){

        from = -1;
        to = -1;
        write = false;

        switch (mode){
            case Hand -> write = true;
            case Hotbar -> {
                if (itemResult.isHotbar()){
                    InvUtils.swap(itemResult.getSlot());
                    write = true;
                }
                else running = false;
            }
            case All, Number -> {
                if (itemResult.isHotbar()){
                    InvUtils.swap(itemResult.getSlot());
                    write = true;
                }else if (!itemResult.isOffhand()){
                    FindItemResult empty = InvUtils.findEmpty();
                    if (empty.isHotbar()){
                        InvUtils.quickMove().from(itemResult.getSlot()).to(empty.getSlot());
                        InvUtils.swap(empty.getSlot());
                        from = itemResult.getSlot();
                        to = empty.getSlot();
                        write = true;
                    }else if (empty.found() && !empty.isOffhand()){
                        FindItemResult written = InvUtils.findInHotbar(Items.WRITTEN_BOOK);
                        InvUtils.quickMove().from(written.getSlot()).to(empty.getSlot());
                        InvUtils.quickMove().from(itemResult.getSlot()).to(written.getSlot());
                        InvUtils.swap(written.getSlot());
                        from = itemResult.getSlot();
                        to = written.getSlot();
                        write = true;
                    }else running = false;
                }else running = false;
            }
        }
    }



    private void writeBook() {
        if (InvUtils.findInHotbar(Items.WRITABLE_BOOK).getHand() == null || !InvUtils.findInHotbar(Items.WRITABLE_BOOK).isMainHand()){
            warning("no book in main hand");
        }
        pages.clear();

        readChar();


        for (int pageI = 0; pageI < noOfPages; pageI++) {
            pageSb.setLength(0);
            boolean endOfStream = false;

            for (int lineI = 0; lineI < 13; lineI++) {
                lineSb.setLength(0);
                float width = 0;
                boolean endOfStream2 = false;

                while (true) {
                    float charWidth = ((TextHandlerAccessor) mc.textRenderer.getTextHandler()).getWidthRetriever().getWidth(nextChar, Style.EMPTY);
                    if (nextChar == '\n') {
                        if (!readChar()) endOfStream2 = true;
                        break;
                    }
                    if (width + charWidth < LINE_WIDTH) {
                        lineSb.appendCodePoint(nextChar);
                        width += charWidth;

                        if (!readChar()) {
                            endOfStream2 = true;
                            break;
                        }
                    } else break;
                }

                pageSb.append(lineSb).append('\n');
                if (endOfStream2) {
                    endOfStream = true;
                    break;
                }
            }

            pages.add(NbtString.of(pageSb.toString()));
            if (endOfStream) break;
        }

        assert mc.player != null;
        mc.player.getMainHandStack().putSubTag("pages", pages);
        mc.player.getMainHandStack().putSubTag("author", NbtString.of(mc.player.getName().asString()));
        mc.player.getMainHandStack().putSubTag("title", NbtString.of(name));
        mc.player.networkHandler.sendPacket(new BookUpdateC2SPacket(mc.player.getMainHandStack(), true, mc.player.inventory.selectedSlot));
        noOfBooks--;
    }


    private boolean readChar() {
        if (!stream.hasNext()) {
            IntStream charGenerator = RANDOM.ints(0x20, 0x7f);
            stream = charGenerator.limit(35000).iterator();
            return false;
        }

        nextChar = stream.nextInt();
        return true;
    }

}
