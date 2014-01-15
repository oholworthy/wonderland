# wonderland

Generating random text based on a source text with markov chains.

For the [West London Hack Night](http://www.meetup.com/West-London-Hack-Night/) 2014-01-14.

## Usage

```clojure
wonderland.core=> (def prefix-map (prefix-probabilities (sample-text :sherlock)))

wonderland.core=> (repeatedly 5 (partial generate-words "i want" prefix-map :words 10))
("the case of young openshaw's. what steps did you gain them? you know"
 "the case and a half feet of me, in the colonies, so that"
 "the case for the tide receded. and what conclusions did the sholtos. but"
 "the case with great yellow blotches of lichen upon the ground, shimmering brightly"
 "the case clearly and concisely to you, miss hunter, that we would give")
```
