import { CoreCode } from "./core/coreCode.js";
import { console, queue } from "./main.js";
import { graph } from "./main.js";
import { stack } from "./main.js";
import { ColorIndex } from "./adt/graph.js";
import { SNAP_DISTANCE, SCALE } from "./adt/graph.js";

export class UserCode extends CoreCode {

    #startNode;
    #endNode;
    
    //#region graph setup helpers
    async setup(startStr, endStr) {
        let varNode = graph.varNodes.filter(vN => vN.label.toLowerCase() === startStr)[0];
        if (!varNode) {
            console.outln(`No '${startStr}' node detected.`);
            return false;
        }
        this.#startNode = varNode.neighbors[0];
        if (endStr) {
            varNode = graph.varNodes.filter(vN => vN.label.toLowerCase() === endStr)[0];
            if (!varNode) {
                console.outln(`No '${endStr}' node detected.`);
                return false;
            }
            this.#endNode = varNode.neighbors[0];
        }
        graph.traverse(node =>{ node.colorIndex = 0; });
        graph.edges.forEach(edge => { edge.colorIndex = 0; });
        return true;
    }

    async loadGraph(graphData) {
        const response = await fetch(`https://florinteo.github.io/EduCode/GraphVisualizer/data/${graphData}`);
        const graphString = await response.text();
        graph.fromString(graphString);
        queue.clear();
        stack.clear();
    }

    async isBinaryTree() {
        let result = true;
        graph.nodes.forEach(n => { n.marker = 0; });
        if (!this.#startNode.isTree(2)) {
            console.outln("Error: Not a binary tree!");
            return false;
        }
        return true;
    }

    #delay() {
        return this.tracing ? Infinity : 100;
    }
    //#endregion graph setup helpers

    //#region tree algorithms
    async toPrefixStr() {
        // pick up inputs in the algo
        if (!await this.setup("root") || !await this.isBinaryTree()) {
            return false;
        }
       
        let root = this.#startNode;
        graph.traverse(n => {n.state = 0;});
        stack.clear();
        queue.clear();
        stack.push(root);
        while (stack.size() > 0) {
            let node = stack.pop();
            if (node.neighbors.length > 0) {
                if (node.colorIndex == ColorIndex.Gray) {
                    node.colorIndex = ColorIndex.Yellow;
                    await this.step(this.#delay());
                    stack.push(node);
                    if (node.left) {
                        stack.push(node.left);
                    }
                    if (node.right) {
                        stack.push(node.right);
                    }
                } else {
                    node.colorIndex = ColorIndex.Green;
                    node.state = `${node.label} `
                               + `${node.left ? node.left.state + " " : ""}`
                               + `${node.right ? node.right.state + " " : ""}`;
                    node.state = node.state.trim();
                }
            } else {
                node.colorIndex = ColorIndex.Green;
                node.state = node.label;
            }
            await this.step(this.#delay());
        }
        console.outln(root.state);
        return true;
    }

    async toInfixStr() {
        // pick up inputs in the algo
        if (!await this.setup("root") || !await this.isBinaryTree(this.#startNode)) {
            return;
        }
        
        let root = this.#startNode;
        graph.traverse(n => {n.state = 0;});
        stack.clear();
        queue.clear();
        stack.push(root);
        while (stack.size() > 0) {
            let node = stack.pop();
            if (node.left || node.right) {
                if (node.colorIndex == ColorIndex.Gray) {
                    node.colorIndex = ColorIndex.Yellow;
                    await this.step(this.#delay());
                    stack.push(node);
                    if (node.left) {
                        stack.push(node.left);
                    }
                    if (node.right) {
                        stack.push(node.right);
                    }
                } else {
                    node.colorIndex = ColorIndex.Green;
                    node.state = `(${node.left ? node.left.state + " " : ""}`
                               + `${node.label}`
                               + `${node.right ? " " + node.right.state : ""})`;
                    node.state = node.state.trim();
                }
            } else {
                node.colorIndex = ColorIndex.Green;
                node.state = node.label;
            }
            await this.step(this.#delay());
        }
        console.outln(root.state);
        return true;
    }

    async toPostfixStr() {
        // pick up inputs in the algo
        if (!await this.setup("root") || !await this.isBinaryTree(this.#startNode)) {
            return;
        }
        
        let root = this.#startNode;
        graph.traverse(n => {n.state = 0;});
        stack.clear();
        queue.clear();
        stack.push(root);
        while (stack.size() > 0) {
            let node = stack.pop();
            if (node.neighbors.length > 0) {
                if (node.colorIndex == ColorIndex.Gray) {
                    node.colorIndex = ColorIndex.Yellow;
                    await this.step(this.#delay());
                    stack.push(node);
                    if (node.left) {
                        stack.push(node.left);
                    }
                    if (node.right) {
                        stack.push(node.right);
                    }
                } else {
                    node.colorIndex = ColorIndex.Green;
                    node.state = `${node.left ? node.left.state + " ": ""}`
                               + `${node.right ? node.right.state + " ": ""}`
                               + `${node.label} `;
                    node.state = node.state.trim();
                }
            } else {
                node.colorIndex = ColorIndex.Green;
                node.state = node.label;
            }
            await this.step(this.#delay());
        }
        console.outln(root.state);
        return true;
    }

    async avlCheck() {
        if (!await this.setup("root") || !await this.isBinaryTree()) {
            return false;
        }
        let isSearch = true;
        let isBalanced = true;
        let root = this.#startNode;
        graph.traverse(n => { 
            n.state = 0;
            n.min = undefined;
            n.max = undefined;
            n.error = undefined;
            n.bf = undefined;
        });
        stack.clear();
        queue.clear();
        stack.push(root);
        let varNodes = graph.varNodes.filter(vn => ["A", "B", "C", "D", "E", "F", "G"].includes(vn.label));
        varNodes.forEach(vn => graph.removeVarNode(vn));
        while (stack.size() > 0) {
            let node = stack.pop();
            if (node.colorIndex == ColorIndex.Gray) {
                node.colorIndex = ColorIndex.Yellow;
                await this.step(this.#delay());
                stack.push(node);
                if (node.left) {
                    stack.push(node.left);
                }
                if (node.right) {
                    stack.push(node.right);
                }
            } else {
                let heightLeft = node.left ? node.left.state : 0;
                let heightRight = node.right ? node.right.state : 0;
                node.bf = heightRight - heightLeft;
                node.state = Math.max(heightLeft, heightRight) + 1;
                node.min = node.left ? node.left.min : node;
                node.max = node.right ? node.right.max : node;
                if ((node.left && node.left.error != undefined) || (node.right && node.right.error != undefined)) {
                    node.colorIndex = ColorIndex.Blue;
                    node.error = 0;
                } else if (Math.abs(node.bf) > 1) {
                    node.colorIndex = ColorIndex.Green;
                    node.error = 1;
                    isBalanced = false;
                } else if ((node.left && node.compare(node.left.max) < 0) || (node.right && node.compare(node.right.min) >= 0)) {
                    node.colorIndex = ColorIndex.Red;
                    isSearch = false;
                } else {
                    node.colorIndex = ColorIndex.Gray;
                }
            }
        }
        return {isBalanced, isSearch};
    }

    #avlSetLabel(node, labels, xShift) {
        let label = labels.shift();
        if (node) {
            graph.addVarNode(label, node.x + xShift, node.y - SNAP_DISTANCE[SCALE] / 2);
        }
    }

    async avlSetLabels() {
        let labels = ["A", "B", "C", "D", "E", "F", "G"];
        let avlNode = graph.nodes.find(n => (n.error == 1));
        // set variables for the root node and its children: A, B and C
        this.#avlSetLabel(avlNode, labels, (avlNode === this.#startNode) ? -SNAP_DISTANCE[SCALE] / 4 : 0);
        this.#avlSetLabel(avlNode.left, labels, -SNAP_DISTANCE[SCALE] / 4);
        this.#avlSetLabel(avlNode.right, labels, SNAP_DISTANCE[SCALE] / 4);
        // check left/right heavy
        if (avlNode.bf < 0) {
            // if left-heavy set D, E variables on the children of the left node
            this.#avlSetLabel(avlNode.left.left, labels, -SNAP_DISTANCE[SCALE] / 4);
            this.#avlSetLabel(avlNode.left.right, labels, SNAP_DISTANCE[SCALE] / 4);
            // if left-right heavy, set the F, G variables on the children of the left-right node
            if (avlNode.left.bf > 0) {
                this.#avlSetLabel(avlNode.left.right.left, labels, -SNAP_DISTANCE[SCALE] / 4);
                this.#avlSetLabel(avlNode.left.right.right, labels, SNAP_DISTANCE[SCALE] / 4);
                return "LEFT-right heavy case.";
            } else {
                return "LEFT-left heavy case.";
            }
        } else {
            // if right-heavy set D, E variables on the children of the right node
            this.#avlSetLabel(avlNode.right.left, labels, -SNAP_DISTANCE[SCALE] / 4);
            this.#avlSetLabel(avlNode.right.right, labels, SNAP_DISTANCE[SCALE] / 4);
            // if right-left heavy, set the F, G variables on the children of the right-left node
            if (avlNode.right.bf < 0) {
                this.#avlSetLabel(avlNode.right.left.left, labels, -SNAP_DISTANCE[SCALE] / 4);
                this.#avlSetLabel(avlNode.right.left.right, labels, SNAP_DISTANCE[SCALE] / 4);
                return "RIGHT-left heavy case.";
            } else {
                return "RIGHT-right heavy case.";
            }
        }
    }

    async avlStats() {
        return `nodes=${graph.nodes.length}, maxSteps=${this.#startNode.state}`;
    }
    //#endregion tree algorithms

    //#region graph algorithms
    async runTraversal() {
        // pick up inputs in the algo
        if (!await this.setup("root")) {
            return;
        }
        let root = this.#startNode;
        queue.clear();
        queue.enqueue(root);
        while(queue.size() != 0){
            let node = queue.dequeue();
            node.colorIndex=ColorIndex.Red;
            for(const n of node.neighbors){
                if(n.colorIndex == ColorIndex.Gray){
                    n.colorIndex = ColorIndex.Green;
                    queue.enqueue(n);
                    await this.step(this.#delay());
                }
            }
            node.colorIndex=ColorIndex.Green;
            await this.step(this.#delay());
        }
    }

    async runPartitioning() {
        let crtPartition = 0;
        queue.clear();
        graph.nodes.forEach(n => {
            n.colorIndex = ColorIndex.Gray;
            n.state = 0;
            queue.enqueue(n); });
        await this.step(this.#delay());
        while(queue.size() > 0) {
            crtPartition++;
            let node = queue.dequeue();
            graph.traverse(n => { 
                n.state = crtPartition;
                n.colorIndex = (crtPartition == ColorIndex.length) ? 1 : crtPartition + 1;},
                node);
            await this.step(this.#delay());
            let again;
            do {
                again = false;
                for(let i = queue.size(); i > 0; i--) {
                    let node2 = queue.dequeue();
                    if (node2.state != 0) {
                        continue;
                    }
                    let isTouching = false;
                    node2.neighbors.forEach(n => { if (n.state != 0) { isTouching = true; }});
                    if (isTouching) {
                        graph.traverse(n => {
                            n.state = crtPartition;
                            n.colorIndex = (crtPartition == ColorIndex.length) ? 1 : crtPartition + 1;},
                            node2);
                        again = true;
                        await this.step(this.#delay());
                    } else {
                        queue.enqueue(node2);
                    }
                }
            } while(again);
        }
        return crtPartition;
    }
    
    async runSpanningTree() {
        // pick up inputs in the algo
        if (!await this.setup("root")) {
            return;
        }

        let root = this.#startNode;
        queue.clear();
        queue.enqueue(root);
        while(queue.size() != 0){
            let node = queue.dequeue();
            node.colorIndex=ColorIndex.Red;
            for(const n of node.neighbors){
                if(n.colorIndex == ColorIndex.Gray){
                    n.colorIndex = ColorIndex.Green;
                    let edge = graph.getEdge(node, n);
                    edge.colorIndex = ColorIndex.Green;
                    queue.enqueue(n);
                    await this.step(this.#delay());
                }
            }
            if (node === root) {
                node.toggleColor(1);
            } else {
                node.colorIndex=ColorIndex.Green;
            }
        }
        // remove edges to make the spanning tree more visible
        let noncoloredEdges = graph.edges.filter(e => e.colorIndex == ColorIndex.Gray)
        for(const e of noncoloredEdges){
            graph.removeEdge(e.node1, e.node2);
            graph.removeEdge(e.node2, e.node1);
            await this.step(this.#delay());
        }
    }

    async extractPath(startNode, endNode) {
        if (endNode.state == null) {
            console.outln("No path exists!");
            return;
        }

        let crtNode = endNode;
        let distance = 0;
        while(crtNode != startNode) {
            let edge = graph.getEdge(crtNode, crtNode.state);
            edge.colorIndex = ColorIndex.Green;
            distance += edge.node1.distance(edge.node2);
            crtNode.colorIndex = ColorIndex.Green;
            await this.step(this.#delay());
            crtNode = crtNode.state;
        }
        crtNode.colorIndex = ColorIndex.Green;

        console.outln(`    route distance = ${distance.toFixed(1)}`);
    }

    async runBFS() {
        // pick up inputs in the algo
        if (!await this.setup("start", "end")) {
            return;
        }
        let startNode = this.#startNode;
        let endNode = this.#endNode;

        // clear initial state
        graph.nodes.forEach(n => { n.state = null; });
        queue.clear();
        queue.enqueue(startNode);
        startNode.state = startNode;

        // loop until the queue is empty
        let iterations = 0;
        while(queue.size() !== 0) {
            iterations++;
            let node = queue.dequeue();
            node.colorIndex = ColorIndex.Magenta;
            await this.step(this.#delay());
            for(const n of node.neighbors) {
                if (n.state != null) {
                    continue;
                }
                n.state = node;
                queue.enqueue(n);
                graph.getEdge(node, n).colorIndex = ColorIndex.Yellow;                
                if (n === endNode) {
                    console.outln("    EndNode is found!");
                    queue.clear();
                    break;
                }
                n.colorIndex = ColorIndex.Red;
                await this.step(this.#delay());
            }
            node.colorIndex = ColorIndex.Yellow;
            await this.step(this.#delay());
        }
        await this.extractPath(startNode, endNode);
        console.outln(`    iterations = ${iterations}`);
    }

    async runDijkstra() {
        // pick up inputs in the algo
        if (!await this.setup("start", "end")) {
            return;
        }
        let startNode = this.#startNode;
        let endNode = this.#endNode;

        // clear initial state
        graph.nodes.forEach(n => { n.state = null; n.cost = Infinity; });
        queue.clear();
        queue.enqueue(startNode);
        startNode.state = startNode;
        startNode.cost = 0;

        // loop until the queue is empty
        let iterations = 0;
        while(queue.size() !== 0) {
            iterations++;
            let node = queue.dequeue();
            node.colorIndex = ColorIndex.Magenta;
            await this.step(this.#delay());
            for(const n of node.neighbors) {
                let newCost = node.cost + node.distance(n);
                // if (n.cost < newCost) {
                //     continue;
                // }
                if (n.cost > newCost) {
                    n.state = node;
                    n.cost = newCost;
                    queue.enqueue(n);
                    graph.getEdge(node, n).colorIndex = ColorIndex.Yellow;
                    if (n != endNode) {               
                        n.colorIndex = ColorIndex.Red;
                    }
                    await this.step(this.#delay());
                }
            }
            node.colorIndex = ColorIndex.Yellow;
            await this.step(this.#delay());
        }
        await this.extractPath(startNode, endNode);
        console.outln(`    iterations = ${iterations}`);
    }

    async runAStar() {
        // pick up inputs in the algo
        if (!await this.setup("start", "end")) {
            return;
        }
        let startNode = this.#startNode;
        let endNode = this.#endNode;

        // clear initial state
        graph.nodes.forEach(n => { n.state = null; });
        queue.clear();
        queue.enqueue(startNode);
        startNode.state = startNode;
        startNode.cost = startNode.distance(endNode);

        // loop until the queue is empty
        let iterations = 0;
        while(queue.size() !== 0) {
            iterations++;
            let node = queue.dequeue();
            node.colorIndex = ColorIndex.Magenta;
            await this.step(this.#delay());
            for(const n of node.neighbors) {
                if (n.state != null) {
                    continue;
                }
                n.state = node;
                n.cost = (node.cost - node.distance(endNode)) + node.distance(n) + n.distance(endNode);
                queue.enqueue(n, (n) => { return n.cost; });
                graph.getEdge(node, n).colorIndex = ColorIndex.Yellow;                
                if (n === endNode) {
                    console.outln("    EndNode is found!");
                    queue.clear();
                    break;
                }
                n.colorIndex = ColorIndex.Red;
                await this.step(this.#delay());
            }
            node.colorIndex = ColorIndex.Yellow;
            await this.step(this.#delay());
        }
        await this.extractPath(startNode, endNode);
        console.outln(`    iterations = ${iterations}`);
    }

    async runEulerianCheck() {
        // resets the state in all nodes
        graph.nodes.forEach(n => {
             n.state = 0;
             n.colorIndex = ColorIndex.Gray;
        });

        // compute the in-degree of each node
        graph.nodes.forEach(n => {
            n.neighbors.forEach(neighbor => { neighbor.state++; });
        });

        // check in-degree vs out-degree balance
        let eulerian = true;
        graph.nodes.forEach(n => {
            if (n.state != n.neighbors.length) {
                eulerian = false;
                n.colorIndex = ColorIndex.Red;
            } else {
                n.colorIndex = ColorIndex.Green;
            }
            n.state = 0;
        });
        console.outln("In-degree == Out-degree? " + eulerian);
        await this.step(this.#delay());

        // check if the graph is connected
        if (eulerian && graph.nodes.length > 0) {
            // check if the graph is connected
            graph.traverse(n => { n.state = 1; }, graph.nodes[0]);
            graph.nodes.forEach(n => {
                if (n.state == 1) {
                    n.state = 0;
                } else {
                    n.colorIndex = ColorIndex.Red;
                    eulerian = false;
                }
            });
            console.outln("Graph is connected? " + eulerian);
            await this.step(this.#delay());
        }

        // reset node states on success
        if (eulerian) {
            graph.nodes.forEach(n => {
                n.state = 0;
                n.colorIndex = ColorIndex.Gray;
            });
        }

        return eulerian;
    }
    //#endregion graph algorithms

    /**
     * Entry point for user-defined code.
     */
    async run() {
        console.outln("---- Starting user-defined code! ----");
        let selection = console.getSelection().split(/\s+/);
        let cmd = selection.shift();
        let args = selection;
        switch(cmd.toLowerCase()) {
            case 'loadtree':
                console.outln("Run loadTree(exprTree.txt):");
                await this.loadGraph("exprTree.txt");
                break;
            case 'loadavltree':
                console.outln("Run loadTree(avlTree.txt):");
                await this.loadGraph("avlTree.txt");
                break;
            case 'toprefixstr':
                console.outln("Run toPrefixStr(root):");
                await this.toPrefixStr();
                break;
            case 'toinfixstr':
                console.outln("Run toInfixStr(root):");
                await this.toInfixStr();
                break;
            case 'topostfixstr':
                console.outln("Run toPostfixStr(root):");
                await this.toPostfixStr();
                break;
            case 'avlcheck':
                console.outln("Run avlCheck(root):");
                const {isSearch, isBalanced} = await this.avlCheck();
                console.outln(`search=${isSearch}, balanced=${isBalanced}`);
                if (isSearch) {
                    if (!isBalanced) {
                        console.outln(`${await this.avlSetLabels()}`);    
                    }
                    console.outln(`${await this.avlStats()}`);
                }
                break;
            case 'loadgraph':
                let graphFile = (args.length == 0) ? "graph.txt" : `unit5/${args[0]}.txt`;
                console.outln(`Run loadGraph(${graphFile}):`);
                await this.loadGraph(graphFile);
                break;
            case 'traverse':
                console.outln("Run traverse(root):");
                await this.runTraversal();
                break;
            case 'partition':
                console.outln("Run partition:");
                await this.runPartitioning();
                break;
            case 'spanningtree':
                console.outln("Run spanningTree(root).");
                await this.runSpanningTree();
                break;
            case 'bfs':
                console.outln("Run bfs(start, end):");
                await this.runBFS();
                break;
            case 'dijkstra':
                console.outln("Run dijkstra(start, end):");
                await this.runDijkstra();
                break;
            case 'astar':
            case 'a*':
                console.outln("Run aStar(start, end):");
                await this.runAStar();
                break;
            case 'euleriancheck':
                console.outln("Run eulerianCheck:");
                if (!await this.runEulerianCheck()) {
                    console.out("NOT ");
                }
                console.outln("Eulerian!");
                break;
            case 'euleriancycle':
                console.outln("Run eulerianCycle(root):");
                break;
            case 'euleriancircuit':
                console.outln("Run eulerianCircuit:");
                break;
            default:
                console.outln("Available commands:");
                console.outln("  loadTree / loadAVLTree : loads a sample binary tree.");
                console.outln("    toPrefixStr  : serializes a tree in prefix order.");
                console.outln("    toInfixStr   : serializes a tree in infix order.")
                console.outln("    toPostfixStr : serializes a tree in postfix order.");
                console.outln("    avlCheck     : checks AVL search and balance properties of a tree.");
                console.outln("  --------------");
                console.outln("  loadGraph  : loads a sample graph.");
                console.outln("    traverse     : runs the Traversal algo.");
                console.outln("    partition    : runs the graph partitioning algo.");
                console.outln("    .............");
                console.outln("    spanningTree : runs the Spanning algo.");
                console.outln("    bfs          : runs Breath-First-Search algo.");
                console.outln("    dijkstra     : runs Dijkstra algo.");
                console.outln("    aStar        : runs the A* algo.");
                console.outln("    .............");
                console.outln("    eulerianCheck   : checks if the graph is Eulerian.");
                console.outln("    eulerianCycle   : gets the Eulerian cycle for a given node.");
                console.outln("    eulerianCircuit : runs the Eulerian circuit."   );
                console.outln("::Select word and click <Run> to execute command::");
        }

        console.outln("---- User-defined code ended! ----");
    }
}