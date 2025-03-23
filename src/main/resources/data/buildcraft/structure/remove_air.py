import nbtlib
from nbtlib.tag import List, Compound

def remove_blocks_with_state_9(nbt_file_path, output_file_path):
    # Load the NBT file
    nbt_data = nbtlib.load(nbt_file_path)

    # Check if the file has a 'blocks' section
    if 'blocks' in nbt_data:
        blocks = nbt_data['blocks']

        # Filter out blocks with state 9 while keeping the correct NBT format
        filtered_blocks = List[Compound]([
            block for block in blocks if block.get('state') != 0
        ])

        # Update the NBT structure
        nbt_data['blocks'] = filtered_blocks

    # Save the modified NBT file
    nbt_data.save(output_file_path)
    print(f"Modified NBT file saved as: {output_file_path}")

# Example usage
remove_blocks_with_state_9("oil.nbt", "oil.nbt")
