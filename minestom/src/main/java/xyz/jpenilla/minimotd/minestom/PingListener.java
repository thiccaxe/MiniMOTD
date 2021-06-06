package xyz.jpenilla.minimotd.minestom;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.ResponseData;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

public class PingListener {
  private final MiniMOTD<String> miniMOTD;
  private final LegacyComponentSerializer unusualHexSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
  private final MiniMOTDExtension extension;

  PingListener(final @NonNull MiniMOTDExtension extension, final @NonNull MiniMOTD<String> miniMOTD) {
    this.extension = extension;
    this.miniMOTD = miniMOTD;
  }

  public void handlePing(final @NonNull ServerListPingEvent event) {
    final MiniMOTDConfig cfg = this.miniMOTD.configManager().mainConfig();
    final ResponseData responseData = event.getResponseData();

    final PingResponse<String> response = this.miniMOTD.createMOTD(cfg, responseData.getOnline(), responseData.getMaxPlayer());

    response.playerCount().applyCount(responseData::setOnline, responseData::setMaxPlayer);
    response.motd(responseData::setDescription);
    response.icon(responseData::setFavicon);

    if (response.disablePlayerListHover()) {
      responseData.clearEntries();
    }
    if (response.hidePlayerCount()) {
      // TODO actually hide the players :/
      responseData.setOnline(0);
      responseData.setMaxPlayer(0);
      responseData.clearEntries();
    }
  }
}
