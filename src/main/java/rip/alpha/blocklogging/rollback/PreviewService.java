package rip.alpha.blocklogging.rollback;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.rollback.apply.PreviewBlocks;
import rip.alpha.blocklogging.rollback.message.RollbackMessages;
import rip.alpha.blocklogging.rollback.model.BlockTarget;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.PendingPreview;
import rip.alpha.blocklogging.rollback.model.RollbackPlan;
import rip.alpha.blocklogging.rollback.state.Previews;

import java.util.List;

@RequiredArgsConstructor
public final class PreviewService {

    private final Previews previews = new Previews();

    private final RollbackService rollbacks;

    void show(Player player, Operation operation, RollbackPlan plan) {
        List<BlockPosition> positions = plan.blocks().stream().map(BlockTarget::position).toList();

        PendingPreview previous = this.previews.put(player.getUniqueId(), new PendingPreview(operation, positions));

        if (previous != null)
            PreviewBlocks.revert(player, previous.positions());

        PreviewBlocks.show(player, plan.blocks());
        RollbackMessages.preview(player, plan);
    }

    public void apply(Player player) {
        PendingPreview preview = this.previews.remove(player.getUniqueId());

        if (preview == null) {
            Chat.error(player, "You have no preview to apply.");
            return;
        }

        Operation operation = preview.operation();

        PreviewBlocks.revert(player, preview.positions());
        this.rollbacks.run(player, Operation.of(operation.query().without(QueryFlag.PREVIEW), operation.kind()));
    }

    public void cancel(Player player) {
        PendingPreview preview = this.previews.remove(player.getUniqueId());

        if (preview == null) {
            Chat.error(player, "You have no preview to cancel.");
            return;
        }

        PreviewBlocks.revert(player, preview.positions());
        Chat.info(player, "Preview cancelled.");
    }

    public void discard(Player player) {
        this.previews.remove(player.getUniqueId());
    }
}
