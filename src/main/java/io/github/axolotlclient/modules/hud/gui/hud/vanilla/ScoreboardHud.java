package io.github.axolotlclient.modules.hud.gui.hud.vanilla;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.modules.hud.util.RenderUtil;
import net.minecraft.scoreboard.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardHud extends TextHudEntry implements DynamicallyPositionable {

    public static final Identifier ID = new Identifier("kronhud", "scoreboardhud");
    private static final Scoreboard placeholderScoreboard = new Scoreboard();

    static {
        ScoreboardObjective objective = placeholderScoreboard.method_4884("placeholder", ScoreboardCriterion.DUMMY);
        ScoreboardPlayerScore dark = placeholderScoreboard.getPlayerScore("DarkKronicle", objective);
        dark.setScore(8780);

        ScoreboardPlayerScore moeh = placeholderScoreboard.getPlayerScore("moehreag", objective);
        moeh.setScore(743);

        ScoreboardPlayerScore kode = placeholderScoreboard.getPlayerScore("TheKodeToad", objective);
        kode.setScore(2948);

        placeholderScoreboard.setObjectiveSlot(1, objective);
    }

    public static final ScoreboardObjective placeholder = new ScoreboardObjective(placeholderScoreboard, "Scoreboard", ScoreboardCriterion.DUMMY);

    private final ColorOption backgroundColor = new ColorOption("axolotlclient.backgroundcolor", 0x4C000000);
    private final ColorOption topColor = new ColorOption("axolotlclient.topbackgroundcolor", 0x66000000);
    private final IntegerOption topPadding = new IntegerOption("axolotlclient.toppadding", ID.getPath(), 0, 0, 4);
    private final BooleanOption scores = new BooleanOption("axolotlclient.scores", true);
    private final ColorOption scoreColor = new ColorOption("axolotlclient.scorecolor", 0xFFFF5555);
    private final EnumOption anchor = new EnumOption("axolotlclient.anchor", AnchorPoint.values(), AnchorPoint.MIDDLE_RIGHT.toString());

    public ScoreboardHud() {
        super(200, 146, true);
    }

    @Override
    public void render(float delta) {
        GlStateManager.pushMatrix();
        scale();
        renderComponent(delta);
        GlStateManager.popMatrix();
    }

    @Override
    public void renderComponent(float delta) {
        Scoreboard scoreboard = this.client.world.getScoreboard();
        ScoreboardObjective scoreboardObjective = null;
        Team team = scoreboard.getPlayerTeam(this.client.player.getName().asUnformattedString());
        if (team != null) {
            int t = team.getFormatting().getColorIndex();
            if (t >= 0) {
                scoreboardObjective = scoreboard.getObjectiveForSlot(3 + t);
            }
        }

        ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
        if (scoreboardObjective2 != null) {
            this.renderScoreboardSidebar(scoreboardObjective2);
        }
    }

    @Override
    public void renderPlaceholderComponent(float delta) {
        renderScoreboardSidebar(placeholder);
    }

    // Abusing this could break some stuff/could allow for unfair advantages. The goal is not to do this, so it won't
    // show any more information than it would have in vanilla.
    private void renderScoreboardSidebar(ScoreboardObjective objective) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(objective);
        List<ScoreboardPlayerScore> filteredScores = scores.stream().filter((testScore) ->
                testScore.getPlayerName() != null && !testScore.getPlayerName().startsWith("#")
        ).collect(Collectors.toList());

        if (filteredScores.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(filteredScores, scores.size() - 15));
        } else {
            scores = filteredScores;
        }

        List<Pair<ScoreboardPlayerScore, String>> scoresWText = Lists.newArrayListWithCapacity(scores.size());
        String text = objective.getDisplayName();
        int displayNameWidth = client.textRenderer.getStringWidth(text);
        int maxWidth = displayNameWidth;
        int spacerWidth = client.textRenderer.getStringWidth(": ");

        ScoreboardPlayerScore scoreboardPlayerScore;
        String formattedText;
        for (
                Iterator<ScoreboardPlayerScore> scoresIterator = scores.iterator();
                scoresIterator.hasNext();
                maxWidth = Math.max(maxWidth, client.textRenderer.getStringWidth(formattedText) + spacerWidth + client.textRenderer.getStringWidth(Integer.toString(scoreboardPlayerScore.getScore())))
        ) {
            scoreboardPlayerScore = scoresIterator.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            formattedText = Team.decorateName(team, scoreboardPlayerScore.getPlayerName());
            scoresWText.add(new Pair<>(scoreboardPlayerScore, formattedText));
        }
        maxWidth = maxWidth + 6;

        int scoresSize = scores.size();
        int scoreHeight = scoresSize * 9;
        int fullHeight = scoreHeight + 11 + topPadding.get() * 2;

        boolean updated = false;
        if (fullHeight + 1 != height) {
            setHeight(fullHeight + 1);
            updated = true;
        }
        if (maxWidth + 1 != width) {
            setWidth(maxWidth + 1);
            updated = true;
        }
        if (updated) {
            onBoundsUpdate();
        }

        Rectangle bounds = getBounds();

        int renderX = bounds.x() + bounds.width() - maxWidth;
        int renderY = bounds.y() + (bounds.height() / 2 - fullHeight / 2) + 1;

        int scoreX = renderX + 4;
        int scoreY = renderY + scoreHeight + 10;
        int num = 0;
        int textOffset = scoreX - 4;

        for (Pair<ScoreboardPlayerScore, String> scoreboardPlayerScoreTextPair : scoresWText) {
            ++num;
            ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboardPlayerScoreTextPair.getLeft();
            String scoreText = scoreboardPlayerScoreTextPair.getRight();
            String score = String.valueOf(scoreboardPlayerScore2.getScore());
            int relativeY = scoreY - num * 9 + topPadding.get() * 2;

            if (background.get() && backgroundColor.get().getAsInt() > 0) {
                if (num == scoresSize) {
                    RenderUtil.drawRectangle(
                            textOffset, relativeY - 1, maxWidth, 10, backgroundColor.get().getAsInt()
                    );
                } else if (num == 1) {
                    RenderUtil.drawRectangle(
                            textOffset,
                           relativeY, maxWidth, 10, backgroundColor.get()
                    );
                } else {
                    RenderUtil.drawRectangle(
                            textOffset, relativeY, maxWidth, 9, backgroundColor.get()
                    );
                }
            }

            if (shadow.get()) {
                client.textRenderer.drawWithShadow(scoreText, (float) scoreX, (float) relativeY, -1);
            } else {
                client.textRenderer.draw(scoreText, scoreX, relativeY, -1);
            }
            if (this.scores.get()) {
                drawString(score,
                        (float) (scoreX + maxWidth - client.textRenderer.getStringWidth(score) - 6), (float) relativeY,
                        scoreColor.get().getAsInt(), shadow.get());
            }
            if (num == scoresSize) {
                // Draw the title
                if (background.get()) {
                    RenderUtil.drawRectangle(textOffset, relativeY - 10 - topPadding.get() * 2 - 1, maxWidth, 10 + topPadding.get() * 2, topColor.get());
                }
                float title = (renderX + (maxWidth - displayNameWidth) / 2F);
                if (shadow.get()) {
                    client.textRenderer.drawWithShadow(text, title, (float) (relativeY - 9) - topPadding.get(), -1);
                }
                else {
                    client.textRenderer.draw(text, (int) title, (relativeY - 9), -1);
                }
            }
        }

        if (outline.get() && outlineColor.get().getAlpha() > 0) {
            RenderUtil.drawOutline(textOffset, bounds.y(), maxWidth, fullHeight + 2, outlineColor.get());
        }
    }

    @Override
    public List<OptionBase<?>> getConfigurationOptions() {
        List<OptionBase<?>> options = super.getConfigurationOptions();;
        options.add(topColor);
        options.add(scores);
        options.add(scoreColor);
        options.add(anchor);
        options.add(topPadding);
        options.remove(textColor);
        return options;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public AnchorPoint getAnchor() {
        return AnchorPoint.valueOf(anchor.get());
    }
}
