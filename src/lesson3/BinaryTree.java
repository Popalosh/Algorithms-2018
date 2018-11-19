package lesson3;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Attention: comparable supported but comparator is not
@SuppressWarnings("WeakerAccess")
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        T value;

        Node<T> left = null;

        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        } else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        return true;
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    @Override
    public boolean remove(Object o) {
        if (root == null)
            return false;
        else if (size == 1) {
            root = null;
            return true;
        } else {
            T title = (T) o;
            remove(root, title);
            size--;
            return (contains(o));
        }
    }

    public Node<T> remove(Node<T> node, T title) {
        Node<T> trie = node;
        int comparison = title.compareTo(node.value);
        if (comparison < 0) {
            trie.left = remove(trie.left, title);
        } else if (comparison > 0) {
            trie.right = remove(node.right, title);
        } else if (trie.right != null) {
            trie.value = minimum(trie.right).value;
            trie.right = remove(trie.right, trie.value);
        } else {
            if (trie.left != null) {
                trie.value = maximum(trie.left).value;
                trie.left = remove(trie.left, trie.value);
            } else {
                trie = null;
            }
        }
        return trie;
    }

    private Node<T> minimum(Node<T> title) {
        if (title.left == null) return title;
        while (title.left != null) {
            title = title.left;
        }
        return title;
    }

    private Node<T> maximum(Node<T> node) {
        if (node.right == null) return node;
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    private void setNode(boolean onRight, Node<T> parentNode, Node<T> currentNode) {
        if (onRight) {
            parentNode.right = currentNode;
        } else parentNode.left = currentNode;
    }

    public class BinaryTreeIterator implements Iterator<T> {

        private Node<T> current;

        private BinaryTreeIterator() {
            while (current != null) {
                current = root;
            }
        }

        /**
         * Поиск следующего элемента
         * Средняя
         */
        private Node<T> findNext() {
            if (size == 0) return null;
            if (current == null) {
                return find(first());
            }
            if (current.value == last()) return null;

            if (current.right != null) {
                Node<T> succsersor = current.right;
                while (succsersor.left != null) {
                    if (succsersor.left != null) {
                        succsersor = succsersor.left;
                    } else return succsersor;
                }
                return succsersor;
            } else {
                Node<T> ancestor = null, succsersor = null;
                if (root != null) {
                    ancestor = root;
                    succsersor = root;
                }
            while (ancestor != current && ancestor != null) {
                int x = current.value.compareTo(ancestor.value);
                if (x < 0) {
                    succsersor = ancestor;
                    if (ancestor.left != null) {
                        ancestor = ancestor.left;
                    } else return null;
                } else {
                    ancestor = ancestor.right;
                }
            }
            return succsersor;
        }
    }

    @Override
    public boolean hasNext() {
        return findNext() != null;
    }

    @Override
    public T next() {
        current = findNext();
        if (current == null) throw new NoSuchElementException();
        return current.value;
    }

    /**
     * Удаление следующего элемента
     * Сложная
     */
    @Override
    public void remove() {
        Node<T> parent = root;
        Node<T> child = root;
        boolean onLeft = false;
        while (child != current) {
            parent = child;
            if (child.value.compareTo(current.value) < 0) {
                child = child.right;
                onLeft = false;
            } else {
                child = child.left;
                onLeft = true;
            }
        }
        if (current.left == null && current.right == null) {
            if (current == root) {
                root = null;
            } else if (onLeft) parent.left = current.right;
            else parent.right = current.right;
        } else if (current.left == null) {
            if (current == root) {
                root = current.right;
            } else if (onLeft) parent.left = current.right;
            else parent.right = current.right;
        } else if (current.right == null) {
            if (current == root) {
                root = current.left;
            } else if (onLeft) parent.left = current.left;
            else parent.right = current.left;
        } else {
            Node<T> minimumChild = current.right;
            Node<T> parentMinChild = minimumChild;
            while (minimumChild.left != null) {
                parentMinChild = minimumChild;
                minimumChild = minimumChild.left;
            }
            if (current == root && parentMinChild == minimumChild) {
                Node<T> rootLeft = root.left;
                root = minimumChild;
                minimumChild.left = rootLeft;
            } else if (current == root && parentMinChild != minimumChild) {
                parentMinChild.left = minimumChild.right;
                root = minimumChild;
                minimumChild.left = current.left;
                minimumChild.right = current.right;
            } else if (parentMinChild == minimumChild) {
                setNode(!onLeft, parent, minimumChild);
            } else {
                parentMinChild.left = minimumChild.right;
                minimumChild.right = current.right;
                minimumChild.left = current.left;
                setNode(!onLeft, parent, minimumChild);
            }
            minimumChild.left = current.left;
        }
        size--;
        current = findNext();
    }

}

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
