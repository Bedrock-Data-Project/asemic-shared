#!/bin/bash

# Create main directory
mkdir -p asemic-docs

# Create subdirectories
mkdir -p asemic-docs/getting-started
mkdir -p asemic-docs/semantic-layer
mkdir -p asemic-docs/data-integration
mkdir -p asemic-docs/analytics-features
mkdir -p asemic-docs/visualization
mkdir -p asemic-docs/advanced-topics
mkdir -p asemic-docs/troubleshooting
mkdir -p asemic-docs/assets

# Function to move a file from Downloads to the appropriate directory
move_file() {
    source="$HOME/Downloads/$1"
    destination="asemic-docs/$2"
    if [ -f "$source" ]; then
        cp "$source" "$destination"
        echo "Moved $1 to $destination"
    else
        echo "Warning: $1 not found in Downloads folder"
    fi
}

# Move files to root directory
move_file "README.md" "README.md"

# Move files to getting-started
move_file "introduction.md" "getting-started/introduction.md"
move_file "installation.md" "getting-started/installation.md"
# move_file "quick-start.md" "getting-started/quick-start.md"

# Move files to semantic-layer
move_file "semantic-layer-overview.md" "semantic-layer/overview.md"
move_file "user-actions-layer.md" "semantic-layer/user-actions-layer.md"
move_file "user-properties-layer.md" "semantic-layer/user-properties-layer.md"
move_file "kpi-layer.md" "semantic-layer/kpi-layer.md"
# move_file "auto-generation.md" "semantic-layer/auto-generation.md"
move_file "semantic-layer-approach.md" "semantic-layer/semantic-layer-approach.md"

# Move files to data-integration
move_file "prerequisites.md" "data-integration/prerequisites.md"
move_file "connecting-data-sources.md" "data-integration/connecting-data-sources.md"
move_file "data-modeling.md" "data-integration/data-modeling.md"
move_file "data-security-compliance.md" "data-integration/data-security-compliance.md"

# Move files to analytics-features
# move_file "segmentation.md" "analytics-features/segmentation.md"
move_file "cohort-analysis.md" "analytics-features/cohort-analysis.md"
move_file "funnel-analysis.md" "analytics-features/funnel-analysis.md"
move_file "custom-metrics.md" "analytics-features/custom-metrics.md"

# Move files to visualization
move_file "chart-types.md" "visualization/chart-types.md"
move_file "dashboard-creation.md" "visualization/dashboard-creation.md"
# move_file "layout-engine.md" "visualization/layout-engine.md"

# Move files to advanced-topics
move_file "query-optimization.md" "advanced-topics/query-optimization.md"
move_file "custom-sql.md" "advanced-topics/custom-sql.md"
move_file "api-integration.md" "advanced-topics/api-integration.md"

# Move files to troubleshooting
# move_file "common-issues.md" "troubleshooting/common-issues.md"
# move_file "faq.md" "troubleshooting/faq.md"

# Move image files
for img in "$HOME/Downloads/assets"/*.{png,jpg,jpeg,gif,svg}; do
    if [ -f "$img" ]; then
        mv "$img" "asemic-docs/assets/"
        echo "Moved $(basename "$img") to asemic-docs/assets/"
    fi
done

echo "Documentation structure created and files moved successfully!"
