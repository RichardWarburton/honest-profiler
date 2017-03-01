/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.view.cell;

import static javafx.scene.paint.Color.DARKRED;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GraphicalShareTableCell extends TableCell<FlatProfileEntry, Double>
{
    private static final double HEIGHT = 8;
    private static final Color TIME_TAKEN_COLOR = DARKRED;

    private final double width;

    public GraphicalShareTableCell(final double width)
    {
        this.width = width;
    }

    @Override
    protected void updateItem(Double timeShare, boolean empty)
    {
        super.updateItem(timeShare, empty);

        if (empty || timeShare == null)
        {
            setText(null);
            setGraphic(null);
            return;
        }

        setGraphic(new Rectangle(timeShare * width, HEIGHT, TIME_TAKEN_COLOR));
    }
}
