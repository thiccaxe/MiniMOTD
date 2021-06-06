/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2021 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xyz.jpenilla.minimotd.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extensions.Extension;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Base64;

public class MiniMOTDExtension extends Extension implements MiniMOTDPlatform<String> {

  private MiniMOTD<String> miniMOTD;

  @Override
  public void initialize() {
    this.miniMOTD = new MiniMOTD<>(this);

    final PingListener pingListener = new PingListener(this, this.miniMOTD);
    MinecraftServer.getGlobalEventHandler().addEventCallback(ServerListPingEvent.class, pingListener::handlePing);

    MinecraftServer.getCommandManager().register(new MinestomCommand(this));

    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      MinecraftServer.getSchedulerManager().buildTask(() -> {
        new UpdateChecker().checkVersion().forEach(this.logger()::info);
      }).schedule();
    }
  }

  public @NonNull MiniMOTD<String> miniMOTD() {
    return this.miniMOTD;
  }

  @Override
  public @NonNull Path dataDirectory() {
    return this.getDataDirectory();
  }

  @Override
  public @NonNull Logger logger() {
    return this.getLogger();
  }

  @NotNull
  @Override
  public String loadIcon(@NonNull BufferedImage image) throws Exception {
    if (!(image.getWidth() == 64 && image.getHeight() == 64)) {
      //throw new IllegalArgumentException("Supplied Image has incorrect size: Needed 64x64, supplied: " + image.getWidth() + "x" + image.getHeight() + "!");
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    ImageIO.write(image, "png", outputStream);
    return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
  }

  @Override
  public void terminate() {}
}
