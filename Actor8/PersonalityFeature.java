// File: c:/ddc/Java/Actor8/PersonalityFeature.java
// Date: Mon Oct 16 17:37:49 2017
// (C) OntoOO/ Dennis de Champeaux

package actor8;

import java.util.*;
import java.io.*;

public class PersonalityFeature {
    public static PersonalityFeature[] allFeatures = {
	new PersonalityFeature("Academic-smart", "No academics"), 
	new PersonalityFeature("Accurate", "Careless"), 
	new PersonalityFeature("Adventurous", "Prudent"),
	new PersonalityFeature("Affluent", "Poor/debts"), 
	new PersonalityFeature("Agile", "Clumsy"), 
	new PersonalityFeature("Aggressive", "Non Aggressive"),
	new PersonalityFeature("Appreciate Humor", "No sense of humor"), 
	new PersonalityFeature("Art curious", "Not interested"), 
	new PersonalityFeature("Artistic (all types)", "No art"), 
	new PersonalityFeature("Balanced", "Unbalanced"), 
	new PersonalityFeature("Brave", "Fearful"), 
	new PersonalityFeature("Broad-minded", "Close-minded"),
	new PersonalityFeature("Carefree", "Worried"),
	new PersonalityFeature("Careful", "Careless"),
	new PersonalityFeature("Caring", "Neglecting"),
	new PersonalityFeature("Cheerful", "Gloomy"),
	new PersonalityFeature("Clear minded", "Fuzzy"),
	new PersonalityFeature("Considered", "Disregarded"),
	new PersonalityFeature("Conventional", "Bohemian"),
	new PersonalityFeature("Creative", "Ungifted"),
	new PersonalityFeature("Critical", "Uncritical"),
	new PersonalityFeature("Curious", "Indifferent"),
	new PersonalityFeature("Decisive", "Indecisive"),
	new PersonalityFeature("Demanding", "Undemanding"),
	new PersonalityFeature("Direct", "Indirect"),
	new PersonalityFeature("Drives OK", "Drives not OK"), 
	new PersonalityFeature("Educated", "Uneducated"), 
	new PersonalityFeature("Emancipated", "Un-Emancipated"), 
	new PersonalityFeature("Emotional", "Unemotional"),
	new PersonalityFeature("Emotionally stable", "Moody"),
	new PersonalityFeature("Empathetic", "Asperger/ Autistic"), 
	new PersonalityFeature("Expressive", "Inexpressive"), 
	new PersonalityFeature("Fair", "Hypocritical/ double dipper"), 
	new PersonalityFeature("Flexible", "Inflexible"), 
	new PersonalityFeature("Friendly", "Unfriendly"), 
	new PersonalityFeature("Generous", "Miserly"), 
	new PersonalityFeature("Healthy habits", "Unhealthy habits"),
	new PersonalityFeature("Helpful", "Unhelpful"),
	new PersonalityFeature("High strung", "Relaxed"),
	new PersonalityFeature("Honest", "Lying/ dishonest"), 
	new PersonalityFeature("Humorous", "No-humor"),
	new PersonalityFeature("Independent", "Dependent"), 
	new PersonalityFeature("Interested", "Boring"), 
	new PersonalityFeature("Introspective", "Unthoughtful"), 
	new PersonalityFeature("Introvert", "Extrovert"), 
	new PersonalityFeature("Judgmental", "Tolerant"), 
	new PersonalityFeature("Laconic", "Long-winded"), 
	new PersonalityFeature("Leader", "Follower"), 
	new PersonalityFeature("Lean", "Rubenesque"), 
	new PersonalityFeature("Liberal", "Conservative"), 
	new PersonalityFeature("Low maintenance", "High maintenance"), 
	new PersonalityFeature("Mature", "Green"),
	new PersonalityFeature("Merry", "Sorrowful"),
	new PersonalityFeature("Modest", "Showy"),
	new PersonalityFeature("Multi lingual", "Single lingual"), 
	new PersonalityFeature("Nice", "Nasty"),
	new PersonalityFeature("No TV", "TV couch potato"), 
	new PersonalityFeature("Objective", "Subjective"), 
	new PersonalityFeature("Obligation aware", "Rights aware"),
	new PersonalityFeature("Observant", "Inattentive"),
	new PersonalityFeature("Orderly", "Disorderly"),
	new PersonalityFeature("Own income", "Leach"),
	new PersonalityFeature("Patience", "Impatience"),
	new PersonalityFeature("Perseverance", "Indolence"),
	new PersonalityFeature("Pessimistic", "Optimistic"), 
	new PersonalityFeature("Pragmatic", "Idealistic"),
	new PersonalityFeature("Punctual", "Tardy"),
	new PersonalityFeature("Quiet", "Communicative"),
	new PersonalityFeature("Rational", "Irrational"),
	new PersonalityFeature("Realistic", "Fanciful"), 
	new PersonalityFeature("Reliable", "Unreliable"), 
	new PersonalityFeature("Responsible", "Irresponsible"),
	new PersonalityFeature("Sensitive", "Insensitive"),
	new PersonalityFeature("Self-confident", "Insecure"),
	new PersonalityFeature("Self-reliant", "Subservient"),
	new PersonalityFeature("Serious", "Flippant"),
	new PersonalityFeature("Sharp", "Slow"),
	new PersonalityFeature("Skeptical", "Believing"),
	new PersonalityFeature("Social", "Asocial"), 
	new PersonalityFeature("Solid", "Tenuous"), 
	new PersonalityFeature("Sophisticated", "Inexperienced"),
	new PersonalityFeature("Spontaneous", "Planned"),
	new PersonalityFeature("Sportive", "Non-sportive"),
	new PersonalityFeature("Street-smart", "Inexperienced"), 
	new PersonalityFeature("Strict", "Lenient"), 
	new PersonalityFeature("Strong", "Loose"), 
	new PersonalityFeature("Tactful", "Tactless"), 
	new PersonalityFeature("Talkative", "Listening"), 
	new PersonalityFeature("Team player", "Antagonistic"), 
	new PersonalityFeature("Thinking", "Feeling"),
	new PersonalityFeature("Thoughtful", "Unthoughtful"),
	new PersonalityFeature("Thrifty", "Spendthrift"),
	new PersonalityFeature("Trustful", "Untrusting"),
	new PersonalityFeature("Trustworthy", "Untrustworthy"),
	new PersonalityFeature("Verbose", "Succinct"), 
	new PersonalityFeature("Well-mannered", "Unmannerly"), 
	new PersonalityFeature("Well-bred", "Ignoble"),
 
	// new PersonalityFeature("", ""),
	/*
	// BM ...
	new PersonalityFeature("Expressive", "Reserved"),
	new PersonalityFeature("Observant", "Introspective"),
	new PersonalityFeature("Tough-minded", "Friendly"),
	new PersonalityFeature("Scheduling", "Probing"),
	*/
    };
    public static PersonalityFeature[] getAllFeatures() { return allFeatures; }
    public static int numberOfFeatures = allFeatures.length;
    private String left;
    private String right;
    public String getLeft() { return left; }
    public String getRight() { return right; }
    public PersonalityFeature(String l, String r) {
	left = l; right = r;
    }
    public String ascii() { return "[ " + left + " --- " + right + " ]"; }
} // end PersonalityFeature

