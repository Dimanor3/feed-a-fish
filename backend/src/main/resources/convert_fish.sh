mkdir -p trans_fish
for img in ./fish/*.jpg; do
    convert "./opauqe_fish/$img" -fuzz 12% -transparent "#0001fb" -transparent "white" "./trans_fish/$(basename "$img").png"
    touch -t 201111111111 "./trans_fish/$(basename "$img").png"
done
