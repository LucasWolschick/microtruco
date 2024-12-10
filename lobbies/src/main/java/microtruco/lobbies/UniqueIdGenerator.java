package microtruco.lobbies;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class UniqueIdGenerator {
    private static final List<String> NOUNS = List.of("ant", "apple", "arrow", "baby", "bag", "ball", "banana", "bear",
            "beard", "bed", "bee", "bell", "bench", "bird", "blanket", "blob", "blue", "boat", "book", "bottle", "box",
            "bread", "bridge", "bucket", "bus", "butter", "cactus", "cage", "camel", "car", "carpet", "castle", "cat",
            "ceiling", "chair", "cheese", "chicken", "child", "circle", "city", "cliff", "clock", "cloud", "coconut",
            "coffee", "comb", "couch", "cow", "cucumber", "cup", "curtain", "desert", "dog", "dolphin", "donkey",
            "door", "duck", "egg", "elephant", "family", "fence", "field", "fire", "fish", "flower", "flute", "forest",
            "fork", "fox", "friend", "frog", "game", "garden", "giraffe", "glass", "goat", "goose", "grape",
            "grasshopper", "green", "hammer", "hammock", "hat", "helmet", "honey", "hook", "horn", "horse", "house",
            "ice", "iguana", "ink", "island", "jellyfish", "kangaroo", "key", "kitchen", "knife", "ladder", "lamp",
            "lantern", "leaf", "leopard", "lighthouse", "lion", "lobster", "marble", "math", "meadow", "melon",
            "microscope", "milk", "monkey", "moon", "motor", "mountain", "mushroom", "music", "needle", "nest",
            "orange", "ostrich", "owl", "pants", "paper", "parrot", "peach", "pearl", "pelican", "pen", "pepper",
            "phone", "piano", "pig", "pink", "plane", "plank", "plate", "polar", "postcard", "pumpkin", "purple",
            "pyramid", "quail", "quartz", "rabbit", "raft", "rain", "rake", "red", "reed", "ribbon", "ring", "river",
            "riverbank", "road", "rock", "salt", "sand", "scarf", "school", "sea", "seesaw", "shark", "sheep", "shield",
            "shirt", "shoe", "shrimp", "skirt", "skull", "snake", "snow", "sofa", "spider", "spoon", "stadium", "star",
            "straw", "sugar", "suitcase", "sun", "swan", "table", "tambourine", "tap", "tea", "teacher", "tiger", "toy",
            "train", "tree", "turtle", "village", "watch", "water", "wind", "window", "wolf", "yellow");

    private Random random = new Random();

    public String generate() {
        var words = 4;
        var id = new StringBuilder();
        for (var i = 0; i < words; i++) {
            id.append(NOUNS.get(random.nextInt(NOUNS.size())));
            if (i < words - 1) {
                id.append("-");
            }
        }
        return id.toString();
    }
}
