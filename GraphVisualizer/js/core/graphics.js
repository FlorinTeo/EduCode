const GRID_SPACING = [40, 20, 10];
const GRID_COLOR = '#EEEBDD';
const GRID_WIDTH = [1, 0.8, 0.5];

export class Graphics {
    /*
    Class members:
        hCanvas - the canvas html element painted by this graphics engine
        width   - the canvas width
        height  - the canvas height
    */

    constructor(hCanvas) {
        this.width = hCanvas.width;
        this.height = hCanvas.height;
        this.hCanvas = hCanvas;
    }

    // resizes the drawing canvas
    resize(width, height) {
        this.width = width;
        this.height = height;
        this.hCanvas.width = width;
        this.hCanvas.height = height;
        this.clear();
    }

    // draws a line between the given coordinates, in given color
    drawLine(fromX, fromY, toX, toY, marginFrom, marginTo, width, color) {
        let dX = toX - fromX;
        let dY = toY - fromY;
        let length = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
        if (length == 0) {
            return;
        }
        fromX += dX * marginFrom/length;
        fromY += dY * marginFrom/length;
        toX -= dX * marginTo/length;
        toY -= dY * marginTo/length;

        let context = this.hCanvas.getContext("2d");
        context.beginPath();
        context.setLineDash([]); 
        context.strokeStyle = color;
        context.lineWidth = width;
        context.moveTo(fromX, fromY);
        context.lineTo(toX, toY);
        context.stroke();
    }

    drawSelection(fromX, fromY, toX, toY, width, color) {
        let context = this.hCanvas.getContext("2d");
        context.beginPath();
        context.setLineDash([4, 4]); 
        context.strokeStyle = color;
        context.lineWidth = width;
        context.moveTo(fromX, fromY);
        context.lineTo(toX, toY);
        context.stroke();
    }

    // draws a node as a labeled circle
    drawNode(label, x, y, radius, width, font, fillColor, halo) {
        let context = this.hCanvas.getContext("2d");
        context.beginPath();
        context.setLineDash([]); 
        context.arc(x, y, radius, 0, 2 * Math.PI, false);
        context.fillStyle = fillColor;
        context.fill();
        context.lineWidth = width;
        context.strokeStyle = 'black';
        context.stroke();
        if (font != null) {
            context.font = font;
            context.textAlign = "center";
            context.fillStyle = 'black';
            context.fillText(label, x, y + 4);
        }
        if (halo) {
            context.beginPath();
            //context.setLineDash([4, 2]);
            context.arc(x, y, radius + 1, 0, 2 * Math.PI, false);
            context.lineWidth = 2*width;
            context.strokeStyle = 'blue';
            context.stroke();
        }
    }

    // draws a varNode label
    drawVarNode(label, x, y, font) {
        let context = this.hCanvas.getContext("2d");
        context.font = font;
        context.textAlign = "center";
        // draw the label background
        let labelMetrics = context.measureText(label);
        let labelWidth = labelMetrics.width;
        let labelHeight = labelMetrics.actualBoundingBoxAscent + labelMetrics.actualBoundingBoxDescent;
        context.fillStyle = 'white';
        context.fillRect(x - labelWidth / 2 - 2, y - labelHeight / 2 - 2, labelWidth + 4, labelHeight + 4);
        // draw the label
        context.fillStyle = 'black';
        context.fillText(label, x, y + 4);
        // calculate the side of the null marker based on the size of the rendered character "O" for the current font
        let nullMetrics = context.measureText("O");
        let nullSide = nullMetrics.actualBoundingBoxAscent + nullMetrics.actualBoundingBoxDescent;
        return [labelWidth, labelHeight, nullSide];
    }

    drawNull(topLeftX, topLeftY, width, height) {
        let context = this.hCanvas.getContext("2d");
        context.strokeStyle = 'gray';
        context.lineWidth = 1;
        context.strokeRect(topLeftX, topLeftY, width, height);
        context.beginPath();
        context.setLineDash([]); 
        context.strokeStyle = 'black';
        context.moveTo(topLeftX, topLeftY + height);
        context.lineTo(topLeftX + width, topLeftY);
        context.stroke();
    }

    drawArrow(fromX, fromY, toX, toY, marginTo, arrowLength, arrowWidth, lineWidth, color, isFilled) {
        let dX = toX - fromX;
        let dY = toY - fromY;
        let length = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
        let pointX = toX - dX * marginTo / length;
        let pointY = toY - dY * marginTo / length;
        length -= marginTo;
        dX = pointX - fromX;
        dY = pointY - fromY;
        let baseX = pointX - dX * arrowLength / length;
        let baseY = pointY - dY * arrowLength / length;
        let xA = baseX + arrowWidth * dY / length;
        let yA = baseY - arrowWidth * dX / length;
        let xB = baseX - arrowWidth * dY / length;
        let yB = baseY + arrowWidth * dX / length;
        let context = this.hCanvas.getContext("2d");
        context.beginPath();
        context.setLineDash([]); 
        context.strokeStyle = color;
        context.lineWidth = lineWidth;
        context.moveTo(xA, yA);
        context.lineTo(pointX, pointY);
        context.lineTo(xB, yB);
        if (isFilled) {
            context.lineTo(xA, yA);
            context.fillStyle = 'black';
            context.fill();
        }
        context.stroke();
    }

    /**
     * Draws a vertical square bracket with the top-right corner at
     * the given x,y, with given height, painted in given color.
     * @param {*} fromX top-right X coordinate of the margin area
     * @param {*} fromY top-right y coordinate of the margin area
     * @param {*} height height of the margin
     * @param {*} color color to use for drawing the margin
     * @returns the width and height of the drawn area
     */
    drawVMargin(fromX, fromY, height, color) {
        let context = this.hCanvas.getContext("2d");
        context.beginPath();
        context.setLineDash([]); 
        context.strokeStyle = color;
        context.lineWidth = 1;
        context.moveTo(fromX, fromY);
        context.lineTo(fromX-4, fromY);
        context.lineTo(fromX-4, fromY + height);
        context.lineTo(fromX, fromY + height);
        context.stroke();
        return [4, height];
    }

    /**
     * Draws a vertical square bracket with the top-right corner at
     * the given x,y, with given height, painted in given color.
     * @param {*} fromX top-right X coordinate of the margin area
     * @param {*} fromY top-right y coordinate of the margin area
     * @param {*} height height of the margin
     * @param {*} color color to use for drawing the margin
     * @returns the width and height of the drawn area
     */
    drawHMargin(fromX, fromY, width, color) {
        let context = this.hCanvas.getContext("2d");
        context.beginPath();
        context.setLineDash([]); 
        context.strokeStyle = color;
        context.lineWidth = 1;
        context.moveTo(fromX, fromY);
        context.lineTo(fromX, fromY + 4);
        context.lineTo(fromX + width, fromY + 4);
        context.lineTo(fromX + width, fromY);
        context.stroke();
        return [width, 4];
    }

    /**
     * Given a label/text, it gets the pixel width and height rendered text.
     * @param {*} text the text to be measured
     * @returns the width and height of the rendered area
     */
    measureText(text) {
        let context = this.hCanvas.getContext("2d");
        context.font = '14px Consolas';
        context.textAlign = 'left';
        context.fillStyle = 'black';
        let textMetrics = context.measureText(text);
        return [textMetrics.width, textMetrics.fontBoundingBoxAscent + textMetrics.fontBoundingBoxDescent];
    }

    /**
     * Writes the given text in an area of the canvas identified by
     * its top-right x, y corner, and vertically allign to the given
     * hight down from the top-right corner.
     * @param {*} fromX top-right x coordinate of the text area.
     * @param {*} fromY top-right y coordinate of the text area.
     * @param {*} height vertical text alignment down from the top-right corner.
     * @param {*} color color of the text
     * @param {*} text the text to be drawn
     * @returns the width and height of the drawn area
     */
    drawText(fromX, fromY, text) {
        let [w, h] = this.measureText(text);
        let context = this.hCanvas.getContext("2d");
        context.beginPath();
        context.setLineDash([]); 
        context.font = '14px Consolas';
        context.textAlign = 'left';
        context.fillStyle = 'black';
        context.fillText(text, fromX, fromY);
        context.stroke();
        return [w, h];
    }

    // clears the drawing canvas
    clear(grid, scale) {
        let context = this.hCanvas.getContext("2d");
        context.fillStyle = 'white';
        context.fillRect(0, 0, this.hCanvas.width, this.hCanvas.height);
        if (grid) {
            context.strokeStyle = GRID_COLOR;
            context.lineWidth = GRID_WIDTH[scale];
            
            // Draw vertical lines
            for (let x = GRID_SPACING[scale]; x <= this.hCanvas.width; x += GRID_SPACING[scale]) {
                context.beginPath();
                context.moveTo(x, 0);
                context.lineTo(x, this.hCanvas.height);
                context.stroke();
            }
            
            // Draw horizontal lines
            for (let y = GRID_SPACING[scale]; y <= this.hCanvas.height; y += GRID_SPACING[scale]) {
                context.beginPath();
                context.moveTo(0, y);
                context.lineTo(this.hCanvas.width, y);
                context.stroke();
            }
        }
    }
}