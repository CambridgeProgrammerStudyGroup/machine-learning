import Data.Char
import Data.Foldable (toList)
import Data.Sequence (Seq, replicate, adjust)

data Mem = Mem Int Int (Seq Int)

bf :: Char -> Mem -> Mem
bf '+' (Mem progc outc mem) = (Mem progc outc       (adjust (+1) outc mem))
bf '>' (Mem progc outc mem) = (Mem progc (outc+1) mem)
bf '<' (Mem progc outc mem) = (Mem progc (outc-1) mem)
bf '[' (Mem progc outc mem) = (Mem progc outc mem)
bf ']' (Mem progc outc mem) = (Mem progc outc mem)
bf '.' (Mem progc outc mem) = (Mem progc outc mem)
bf ',' (Mem progc outc mem) = (Mem progc outc mem)
bf _ mem = mem


output :: Mem -> String
output (Mem _ _ is) = show is
--output (Mem _ _ is) = foldr (++) "\n" $ map show $ takeWhile (not . (==0)) $ toList is


process :: String -> String
-- Don't understand why we have to reverse the program here... Intriguing...
process program = output $ foldr bf (Mem 0 0 (Data.Sequence.replicate 100 0)) (reverse program)


main = interact process