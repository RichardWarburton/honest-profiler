package com.insightfullogic.honest_profiler.ports.javafx.util.report;

import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.LEFT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.RIGHT;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;
import com.insightfullogic.honest_profiler.ports.javafx.model.diff.FlatProfileDiff;

public final class ReportUtil
{

    private static char BOX_HORIZONTAL = 0x2500;
    private static char BOX_VERTICAL = 0x2502;
    private static char BOX_UPANDRIGHT = 0x2514;
    private static char BOX_VERTICALANDRIGHT = 0x251C;

    private static String DROP_STRAIGHT = new String(new char[]
    { BOX_VERTICAL, ' ' });
    private static String DROP_NONLAST = new String(new char[]
    { BOX_VERTICALANDRIGHT, BOX_HORIZONTAL });
    private static String DROP_LAST = new String(new char[]
    { BOX_UPANDRIGHT, BOX_HORIZONTAL });

    private static NumberFormat FMT_PERCENT;

    static
    {
        FMT_PERCENT = NumberFormat.getPercentInstance();
        FMT_PERCENT.setMinimumFractionDigits(2);
        FMT_PERCENT.setMaximumFractionDigits(2);
    }

    public static enum Mode
    {
        TEXT("", " ", ""),
        CSV("\"", "\",\"", "\"");

        private String start;
        private String middle;
        private String end;

        private Mode(String start, String middle, String end)
        {
            this.start = start;
            this.middle = middle;
            this.end = end;
        }

        private PrintWriter start(PrintWriter out)
        {
            out.print(start);
            return out;
        }

        private PrintWriter middle(PrintWriter out)
        {
            out.print(middle);
            return out;
        }

        private PrintWriter end(PrintWriter out)
        {
            out.println(end);
            return out;
        }
    }

    public static void writeStack(PrintWriter out, ProfileNode node)
    {
        Table table = new Table();
        table.addColumn("Method", obj -> obj.toString(), LEFT);
        table.addColumn("Self Time Share", object -> FMT_PERCENT.format(object), RIGHT);
        table.addColumn("Total Time Share", object -> FMT_PERCENT.format(object), RIGHT);

        buildStackTable(node, 0, table, new ArrayList<>());
        table.print(out);
    }

    private static void buildStackTable(ProfileNode node, int level, Table table,
        List<DropLine> dropLines)
    {
        table.addRow(
            indent(dropLines, level)
                + node.getFrameInfo().getClassName()
                + "."
                + node.getFrameInfo().getMethodName(),
            node.getSelfTimeShare(),
            node.getTotalTimeShare());

        if (level > 0)
        {
            dropLines.get(level - 1).decrement();
        }

        List<ProfileNode> children = node.getChildren();

        if (children.size() > 0)
        {
            dropLines.add(new DropLine(children.size()));
            children.forEach(child -> buildStackTable(child, level + 1, table, dropLines));
            dropLines.remove(level);
        }
    }

    public static void writeThread(PrintWriter out, ProfileTree thread, Mode mode)
    {
        // TODO Implement
    }

    public static void writeFlatProfileCsv(PrintWriter out, List<FlatProfileEntry> entries,
        Mode mode)
    {
        mode.start(out);
        out.print("Method");

        mode.middle(out);
        out.print("Self Time %");
        mode.middle(out);
        out.print("Total Time %");

        mode.middle(out);
        out.print("Self Sample #");
        mode.middle(out);
        out.print("Total Sample #");
        mode.middle(out);
        out.print("Profile Sample #");
        mode.end(out);

        entries.forEach(entry ->
        {
            mode.start(out);
            out.print(entry.getFrameInfo().getClassName());
            out.print(".");
            out.print(entry.getFrameInfo().getMethodName());

            mode.middle(out);
            out.printf("%.2f", entry.getSelfTimeShare());
            mode.middle(out);
            out.printf("%.2f", entry.getTotalTimeShare());

            mode.middle(out);
            out.printf("%d", entry.getSelfCount());
            mode.middle(out);
            out.printf("%d", entry.getTotalCount());
            mode.middle(out);
            out.printf("%d", entry.getTraceCount());
            mode.end(out);
        });

        out.flush();
    }

    public static void writeFlatProfileDiffCsv(PrintWriter out, FlatProfileDiff diff, Mode mode)
    {
        mode.start(out);
        out.print("Method");

        mode.middle(out);
        out.print("Base Self Time %");
        mode.middle(out);
        out.print("New Self Time %");
        mode.middle(out);
        out.print("Self Time % Diff");

        mode.middle(out);
        out.print("Base Total Time %");
        mode.middle(out);
        out.print("New Total Time %");
        mode.middle(out);
        out.print("Total Time % Diff");

        mode.middle(out);
        out.print("Base Self Sample #");
        mode.middle(out);
        out.print("New Self Sample #");
        mode.middle(out);
        out.print("Self Sample # Diff");

        mode.middle(out);
        out.print("Base Total Sample #");
        mode.middle(out);
        out.print("New Total Sample #");
        mode.middle(out);
        out.print("Total Sample # Diff");

        mode.middle(out);
        out.print("Base Profile Sample #");
        mode.middle(out);
        out.print("New Profile Sample #");
        mode.middle(out);
        out.print("Profile Sample # Diff");

        mode.end(out);

        diff.getEntries().forEach(entry ->
        {
            mode.start(out);
            out.write(entry.getFullName());

            mode.middle(out);
            out.printf("%.2f", entry.baseSelfTimeShareProperty().get());
            mode.middle(out);
            out.printf("%.2f", entry.newSelfTimeShareProperty().get());
            mode.middle(out);
            out.printf("%.2f", entry.pctSelfChangeProperty().get());

            mode.middle(out);
            out.printf("%.2f", entry.baseTotalTimeShareProperty().get());
            mode.middle(out);
            out.printf("%.2f", entry.newTotalTimeShareProperty().get());
            mode.middle(out);
            out.printf("%.2f", entry.pctTotalChangeProperty().get());

            mode.middle(out);
            out.printf("%d", entry.baseSelfCountProperty().get());
            mode.middle(out);
            out.printf("%d", entry.newSelfCountProperty().get());
            mode.middle(out);
            out.printf(
                "%d",
                entry.newSelfCountProperty().get() - entry.baseSelfCountProperty().get());

            mode.middle(out);
            out.printf("%d", entry.baseTotalCountProperty().get());
            mode.middle(out);
            out.printf("%d", entry.newTotalCountProperty().get());
            mode.middle(out);
            out.printf(
                "%d",
                entry.newTotalCountProperty().get() - entry.baseTotalCountProperty().get());

            mode.middle(out);
            out.printf("%d", entry.baseTraceCountProperty().get());
            mode.middle(out);
            out.printf("%d", entry.newTraceCountProperty().get());
            mode.middle(out);
            out.printf(
                "%d",
                entry.newTraceCountProperty().get() - entry.baseTraceCountProperty().get());

            mode.end(out);
        });

        out.flush();
    }

    private static String indent(List<DropLine> dropLines, int level)
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < level - 1; i++)
        {
            result.append(dropLines.get(i).childrenLeft > 0 ? DROP_STRAIGHT : "  ");
        }

        if (level > 0)
        {
            result.append(dropLines.get(level - 1).childrenLeft > 1 ? DROP_NONLAST : DROP_LAST);
        }

        return result.toString();
    }

    private static class DropLine
    {
        private int childrenLeft;

        private DropLine(int childrenLeft)
        {
            this.childrenLeft = childrenLeft;
        }

        private void decrement()
        {
            childrenLeft--;
        }
    }

    private ReportUtil()
    {
        // Empty Constructor for utility class
    }
}