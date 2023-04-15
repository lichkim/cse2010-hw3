package cse2010.hw3;

/* Block will be used as a type argument */
class Block {
    public int size;
    public int start;
    public int end;

    /**
     * Constructs a block with the given size, start, and end.
     * @param size the size of the block
     * @param start the start index of the block
     * @param end the end index of the block
     */
    public Block(int size, int start, int end) {
        this.size = size;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "(" + size +", " + start + ", " + end + ")";
    }
}

public class MemoryManager {

    private final DLinkedList<Block> heap = new DLinkedList<>();

    /**
     * Constructs a memory manager with the given capacity.
     * @param capacity the capacity of the memory manager
     */
    public MemoryManager(int capacity) {
       heap.addFirst(new Block(capacity, 0, capacity - 1));
    }

    /**
     * Allocates a block of memory with the given size.
     * @param size the size of the requested block
     * @return the allocated block
     * @throws OutOfMemoryException if there is no big-enough available block
     */
    public Block malloc(int size) {
        /**/
        //힙이 비었을 경우 예외처리(모든 메모리가 사용중)
        if (heap.getSize() == 0) {throw new OutOfMemoryException("Currently there are no available memory left. Try later");}

        Node<Block> pointer = heap.getFirst();
        Block returnBlock = null;
        while (!pointer.equals(heap.getTrailer())) {
            if (pointer.getItem().size > size) {
                returnBlock = new Block(size, pointer.getItem().start, pointer.getItem().start + size - 1);
                pointer.setItem(new Block(pointer.getItem().size - size, returnBlock.end + 1, pointer.getItem().end));
                return returnBlock;
            } else if (pointer.getItem().size == size) {
                return heap.remove(pointer);
            }
            pointer = pointer.getNext();
        }
        //trailer에 도착할때 까지 사용가능한 메모리가 없음(모든 힙의 가용 메모리가 size보다 작음) 예외처리
        throw new OutOfMemoryException("Currently there are no aviailable memory left. Try later or Try smaller memory");
    }

    /**
     * Returns the given block to the memory manager.
     * @param block the block to free (i.e, to return to the memory manager)
     */
    public void free(Block block) {
        /**/

        if (heap.getSize() == 0) {
            heap.addFirst(block);
        } else if (heap.getSize() == 1) {
            if (block.end < heap.getFirst().getItem().start) {
                if (block.end + 1 == heap.getFirst().getItem().start) { //합쳐야됨
                    Block addedBlock = new Block(block.size + heap.getFirst().getItem().size, block.start, heap.getFirst().getItem().end);
                    heap.removeFirst();
                    heap.addFirst(addedBlock);
                } else {
                    heap.addFirst(block);
                }
            } else if (block.start > heap.getFirst().getItem().end) {
                if (block.start - 1 == heap.getFirst().getItem().end) { //합쳐야됨
                    Block addedBlock = new Block(block.size + heap.getFirst().getItem().size, heap.getFirst().getItem().start, block.end);
                    heap.removeFirst();
                    heap.addLast(addedBlock);
                } else {
                    heap.addLast(block);
                }
            } else {
                throw new OutOfMemoryException("You freed a wrong block!");
            }
        } else if (heap.getSize() == 2) {
            if (block.end < heap.getFirst().getItem().start) {
                if (block.end + 1 == heap.getFirst().getItem().start) { //합쳐야됨
                    Block addedBlock = new Block(block.size + heap.getFirst().getItem().size, block.start, heap.getFirst().getItem().end);
                    heap.removeFirst();
                    heap.addFirst(addedBlock);
                } else {
                    heap.addFirst(block);
                }
            } else if (block.start > heap.getLast().getItem().end) {
                if (block.start - 1 == heap.getLast().getItem().end) { //합쳐야됨
                    Block addedBlock = new Block(block.size + heap.getLast().getItem().size, heap.getLast().getItem().start, block.end);
                    heap.removeLast();
                    heap.addLast(addedBlock);
                } else {
                    heap.addLast(block);
                }
            } else if (block.start > heap.getFirst().getItem().end && block.end < heap.getLast().getItem().start) {
                if (block.start == heap.getFirst().getItem().end + 1 && block.end == heap.getLast().getItem().start - 1) {
                    Block addedBlock = new Block(heap.getLast().getItem().end - heap.getFirst().getItem().start + 1, heap.getFirst().getItem().start, heap.getLast().getItem().end);
                    heap.removeFirst();
                    heap.removeFirst();
                    heap.addFirst(addedBlock);
                } else if (block.start == heap.getFirst().getItem().end + 1) {
                    Block addedBlock = new Block(heap.getFirst().getItem().size + block.size, heap.getFirst().getItem().start, block.end);
                    heap.removeFirst();
                    heap.addFirst(addedBlock);
                } else if (block.end == heap.getLast().getItem().start - 1) {
                    Block addedBlock = new Block(heap.getLast().getItem().size + block.size, block.start, heap.getLast().getItem().end);
                    heap.removeLast();
                    heap.addLast(addedBlock);
                } else {    //합칠 필요가 없음
                    heap.addAfter(heap.getFirst(), new Node<Block>(block, null, null));
                }
            }
        } else {    //힙에 노드가 3개 이상
            if (block.end < heap.getFirst().getItem().start) {
                if (block.end + 1 == heap.getFirst().getItem().start) { //합쳐야됨
                    Block addedBlock = new Block(block.size + heap.getFirst().getItem().size, block.start, heap.getFirst().getItem().end);
                    heap.removeFirst();
                    heap.addFirst(addedBlock);
                } else {
                    heap.addFirst(block);
                }
            } else if (block.start > heap.getLast().getItem().end) {
                if (block.start - 1 == heap.getLast().getItem().end) { //합쳐야됨
                    Block addedBlock = new Block(block.size + heap.getLast().getItem().size, heap.getLast().getItem().start, block.end);
                    heap.removeLast();
                    heap.addLast(addedBlock);
                } else {
                    heap.addLast(block);
                }
            } else {    //block이 어디 사이에 들어갈 수 있는지 찾자
                Node<Block> pointer = heap.getFirst();
                while (!pointer.equals(heap.getLast())) {
                    if (block.start > pointer.getItem().end && block.end < pointer.getNext().getItem().start) {
                        break;
                    }
                    pointer = pointer.getNext();
                }
                //포인터는 이제 block이 포인터와 포인터 다음 노드 사이에 있도록 하는 노드를 가리킨다. 근데 그게 마지막 노드라면 오류 발생한것임.
                if (pointer.equals(heap.getLast())) {throw new OutOfMemoryException("You freed wrong block!");}
                else {
                    if (block.start == pointer.getItem().end + 1 && block.end == pointer.getNext().getItem().start - 1) {
                        Block addedBlock = new Block(pointer.getNext().getItem().end - pointer.getItem().start + 1, pointer.getItem().start, pointer.getNext().getItem().end);
                        pointer = pointer.getNext().getNext();
                        heap.remove(pointer.getPrev());
                        heap.remove(pointer.getPrev());
                        heap.addBefore(pointer, new Node<Block>(addedBlock, null, null));
                    } else if (block.start == pointer.getItem().end + 1) {
                        Block addedBlock = new Block(block.size + pointer.getItem().size, pointer.getItem().start, block.end);
                        pointer = pointer.getPrev();
                        heap.remove(pointer.getNext());
                        heap.addAfter(pointer, new Node<Block>(addedBlock, null, null));
                    } else if (block.end == pointer.getNext().getItem().start - 1) {
                        Block addedBlock = new Block(block.size + pointer.getNext().getItem().size, block.start, pointer.getNext().getItem().end);
                        heap.remove(pointer.getNext());
                        heap.addAfter(pointer, new Node<Block>(addedBlock, null, null));
                    } else {
                        heap.addAfter(pointer, new Node<Block>(block, null, null));
                    }
                }
            }
        }
    }


    /**
     * Returns the number of free blocks in the memory manager.
     * @return the number of free blocks in the memory manager
     */
    public int getFreeBlockCount() {
        return heap.getSize();
    }

    /**
     * Returns the total size of free blocks in the memory manager.
     * @return the total size of free blocks in the memory manager
     */
    public int getTotalFreeSize() {
        /**/
        int totalFreeSize = 0;
        Node<Block> pointer = heap.getFirst();
        while(!pointer.equals(heap.getTrailer())) {
            totalFreeSize += pointer.getItem().size;
            pointer = pointer.getNext();
        }
        return totalFreeSize;
    }

    @Override
    public String toString() {
        return heap.toString();
    }
}

